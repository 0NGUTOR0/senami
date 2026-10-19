package com.example.demo.service;

import com.example.demo.dto.IncidentRequest;
import com.example.demo.model.IncidentOptions;
import com.example.demo.model.IncidentOptions.*;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;

/**
 * Reads an incident CSV and turns each row into an {@link IncidentRequest}, or a list of problems.
 * Plain Java with no Spring dependencies; patients and duplicates are checked later by the import service.
 */
public final class IncidentCsvImporter {

    public static final int MAX_ROWS = 5000;
    /** Mapping value meaning "leave this value out" (multi-choice columns only). */
    public static final String IGNORE = "__ignore__";

    private IncidentCsvImporter() {
    }

    /** One CSV data row after parsing. {@code request} is null when {@code errors} is not empty. */
    public record Row(int rowNumber, String patientNumber, String externalId,
                      IncidentRequest request, List<String> errors) {
    }

    public record Parsed(List<Row> rows, List<String> fileErrors, List<String> unknownColumns,
                         Map<String, LinkedHashSet<String>> unmapped) {
    }

    // ---------------------------------------------------------------- columns

    private enum Col {
        PATIENT_NUMBER("Patient number", true, "patientnumber", "patientno", "patientid", "patient", "clientnumber"),
        DATE("Date", false, "date", "dateofincident", "incidentdate", "occurredon"),
        TIME("Time", false, "time", "timeofincident", "incidenttime"),
        DATE_TIME("Date and time", false, "datetime", "dateandtime", "occurredat"),
        LOCATION("Location", true, "location"),
        DESCRIPTION("Description", true, "description", "incidentdescription", "details", "whathappened"),
        ANTECEDENTS("Triggers", false, "triggers", "antecedents", "trigger"),
        INTERNAL_FACTORS("Internal factors", false, "internalfactors", "internalfactor"),
        BEHAVIOURS("Behaviours", false, "behaviours", "behaviors", "behaviour", "behavior"),
        HARMFUL_ACTIVITIES("Harmful activities", false, "harmfulactivities", "harmfulactivity"),
        CONSEQUENCES("Consequences", false, "consequences", "consequence"),
        PEOPLE_INVOLVED("People involved", false, "peopleinvolved", "people", "personinvolved"),
        DURATION("Duration", true, "duration"),
        FREQUENCY("Frequency", true, "frequency"),
        SEVERITY("Severity", true, "severity"),
        ON_CALL("On-call involved", true, "oncallinvolved", "oncall"),
        EMERGENCY_SERVICES("Emergency services", false, "emergencyservices", "emergencyservice"),
        NDIS("NDIS reported", true, "ndisreported", "ndis"),
        STRATEGIES("Staff response", false, "staffresponse", "staffresponses", "strategies", "strategy"),
        COMMUNICATION_SUPPORTS("Communication supports", false, "communicationsupports", "communicationsupport"),
        EFFECTIVE("Response effective", true, "responseeffective", "strategieseffective", "effective", "wasresponseeffective"),
        RESPONSE_DESCRIPTION("Response description", false, "responsedescription", "responsedetails"),
        EXTERNAL_ID("External ID", false, "externalid", "reference", "referenceid", "sourceid");

        final String label;
        final boolean required;
        final Set<String> names;

        Col(String label, boolean required, String... names) {
            this.label = label;
            this.required = required;
            this.names = Set.of(names);
        }
    }

    /** The header row written to the template, in order. */
    public static List<String> templateHeaders() {
        List<String> h = new ArrayList<>();
        for (Col c : Col.values()) {
            if (c != Col.DATE_TIME) h.add(c.label);
        }
        return h;
    }

    // ---------------------------------------------------------------- entry point

    public static Parsed parse(byte[] bytes, Map<String, Map<String, String>> mappings) {
        List<String> fileErrors = new ArrayList<>();
        List<String> unknown = new ArrayList<>();
        Map<String, LinkedHashSet<String>> unmapped = new LinkedHashMap<>();
        List<Row> rows = new ArrayList<>();

        if (bytes == null || bytes.length == 0) {
            fileErrors.add("The file is empty.");
            return new Parsed(rows, fileErrors, unknown, unmapped);
        }
        List<List<String>> table = readCsv(decode(bytes));
        int headerAt = 0;
        while (headerAt < table.size() && isBlank(table.get(headerAt))) headerAt++;
        int last = table.size();
        while (last > headerAt && isBlank(table.get(last - 1))) last--;
        if (headerAt >= last) {
            fileErrors.add("The file is empty.");
            return new Parsed(rows, fileErrors, unknown, unmapped);
        }

        // Header row: find each known column, remember the ones we do not recognise.
        List<String> header = table.get(headerAt);
        Map<Col, Integer> index = new EnumMap<>(Col.class);
        for (int i = 0; i < header.size(); i++) {
            String raw = header.get(i).trim();
            if (raw.isEmpty()) continue;
            Col found = null;
            String n = norm(raw);
            for (Col c : Col.values()) {
                if (c.names.contains(n) || norm(c.label).equals(n)) {
                    found = c;
                    break;
                }
            }
            if (found == null) unknown.add(raw);
            else if (index.putIfAbsent(found, i) != null) fileErrors.add("The column \"" + raw + "\" appears more than once.");
        }
        List<String> missing = new ArrayList<>();
        for (Col c : Col.values()) {
            if (c.required && !index.containsKey(c)) missing.add(c.label);
        }
        if (!index.containsKey(Col.DATE_TIME) && !index.containsKey(Col.DATE)) missing.add("Date");
        if (!index.containsKey(Col.DATE_TIME) && !index.containsKey(Col.TIME)) missing.add("Time");
        if (!missing.isEmpty()) {
            fileErrors.add("Missing column(s): " + String.join(", ", missing) + ". Download the template to see the expected columns.");
        }
        if (!fileErrors.isEmpty()) return new Parsed(rows, fileErrors, unknown, unmapped);

        if (last - headerAt - 1 > MAX_ROWS) {
            fileErrors.add("The file has " + (last - headerAt - 1) + " rows; the limit is " + MAX_ROWS + " per import. Split it into smaller files.");
            return new Parsed(rows, fileErrors, unknown, unmapped);
        }
        if (last - headerAt == 1) {
            fileErrors.add("The file has a header row but no data rows.");
            return new Parsed(rows, fileErrors, unknown, unmapped);
        }

        Map<String, Map<String, String>> maps = normaliseMappings(mappings);
        LocalDateTime latest = LocalDateTime.now().plusDays(1);   // tolerance for time zones; the form uses @PastOrPresent

        for (int r = headerAt + 1; r < last; r++) {
            List<String> cells = table.get(r);
            if (isBlank(cells)) continue;
            Ctx x = new Ctx(index, cells, maps, unmapped);
            int rowNumber = r + 1;   // spreadsheet row number (the first line of the file is row 1)

            String patient = x.text(Col.PATIENT_NUMBER);
            if (patient.isEmpty()) x.error("Patient number is required.");

            LocalDateTime occurred = x.occurredAt(latest);

            Location location = x.single(Col.LOCATION, "location", Location.class);
            String description = x.text(Col.DESCRIPTION);
            if (description.isEmpty()) x.error("Description is required.");
            else if (description.length() > 5000) x.error("Description is longer than 5000 characters.");
            String responseDescription = x.text(Col.RESPONSE_DESCRIPTION);
            if (responseDescription.length() > 3000) x.error("Response description is longer than 3000 characters.");

            Set<Antecedent> antecedents = x.multi(Col.ANTECEDENTS, "antecedents", Antecedent.class);
            Set<InternalFactor> internal = x.multi(Col.INTERNAL_FACTORS, "internalFactors", InternalFactor.class);
            Set<Behaviour> behaviours = x.multi(Col.BEHAVIOURS, "behaviours", Behaviour.class);
            Set<HarmfulActivity> harmful = x.multi(Col.HARMFUL_ACTIVITIES, "harmfulActivities", HarmfulActivity.class);
            Set<Consequence> consequences = x.multi(Col.CONSEQUENCES, "consequences", Consequence.class);
            Set<PersonInvolved> people = x.multi(Col.PEOPLE_INVOLVED, "peopleInvolved", PersonInvolved.class);
            Duration duration = x.single(Col.DURATION, "duration", Duration.class);
            Frequency frequency = x.single(Col.FREQUENCY, "frequency", Frequency.class);
            Severity severity = x.single(Col.SEVERITY, "severity", Severity.class);
            Boolean onCall = x.bool(Col.ON_CALL);
            Set<EmergencyService> emergency = x.multi(Col.EMERGENCY_SERVICES, "emergencyServices", EmergencyService.class);
            Boolean ndis = x.bool(Col.NDIS);
            Set<Strategy> strategies = x.multi(Col.STRATEGIES, "strategies", Strategy.class);
            Set<CommunicationSupport> supports = x.multi(Col.COMMUNICATION_SUPPORTS, "communicationSupports", CommunicationSupport.class);
            Boolean effective = x.bool(Col.EFFECTIVE);
            String externalId = x.text(Col.EXTERNAL_ID);
            if (externalId.length() > 100) x.error("External ID is longer than 100 characters.");

            IncidentRequest request = null;
            if (x.errors.isEmpty()) {
                request = new IncidentRequest(occurred, location, description, antecedents, internal, behaviours,
                        harmful, consequences, people, duration, frequency, severity, onCall, emergency, ndis,
                        strategies, supports, effective, responseDescription.isEmpty() ? null : responseDescription);
            }
            rows.add(new Row(rowNumber, patient, externalId.isEmpty() ? null : externalId, request, x.errors));
        }
        return new Parsed(rows, fileErrors, unknown, unmapped);
    }

    // ---------------------------------------------------------------- one row

    private static final class Ctx {
        final Map<Col, Integer> index;
        final List<String> cells;
        final Map<String, Map<String, String>> maps;
        final Map<String, LinkedHashSet<String>> unmapped;
        final List<String> errors = new ArrayList<>();

        Ctx(Map<Col, Integer> index, List<String> cells, Map<String, Map<String, String>> maps,
            Map<String, LinkedHashSet<String>> unmapped) {
            this.index = index;
            this.cells = cells;
            this.maps = maps;
            this.unmapped = unmapped;
        }

        void error(String message) {
            errors.add(message);
        }

        String text(Col c) {
            Integer i = index.get(c);
            if (i == null || i >= cells.size()) return "";
            return cells.get(i).trim();
        }

        Boolean bool(Col c) {
            String v = text(c);
            if (v.isEmpty()) {
                error(c.label + " is required (Yes or No).");
                return null;
            }
            switch (norm(v)) {
                case "yes", "y", "true", "1", "t" -> {
                    return Boolean.TRUE;
                }
                case "no", "n", "false", "0", "f" -> {
                    return Boolean.FALSE;
                }
                default -> {
                    error(c.label + ": \"" + v + "\" is not Yes or No.");
                    return null;
                }
            }
        }

        <E extends Enum<E> & Labeled> E single(Col c, String field, Class<E> type) {
            String v = text(c);
            if (v.isEmpty()) {
                error(c.label + " is required.");
                return null;
            }
            int before = errors.size();
            E e = lookup(c, field, type, v);
            if (e == null && errors.size() == before) error(c.label + " needs a choice; \"" + v + "\" cannot be left out.");
            return e;
        }

        <E extends Enum<E> & Labeled> Set<E> multi(Col c, String field, Class<E> type) {
            Set<E> out = EnumSet.noneOf(type);
            String v = text(c);
            if (v.isEmpty()) return out;
            for (String part : v.split("[|;]")) {
                String p = part.trim();
                if (p.isEmpty()) continue;
                E e = lookup(c, field, type, p);
                if (e != null) out.add(e);
            }
            return out;
        }

        /** Finds the option for a typed value, applying the person's saved mappings first. */
        private <E extends Enum<E> & Labeled> E lookup(Col c, String field, Class<E> type, String raw) {
            String key = norm(raw);
            Map<String, String> m = maps.get(field);
            if (m != null && m.containsKey(key)) {
                String target = m.get(key);
                if (IGNORE.equals(target)) {
                    return null;   // multi-choice: quietly dropped; for single choice the empty result is caught below
                }
                try {
                    return Enum.valueOf(type, target);
                } catch (IllegalArgumentException ex) {
                    // a stale mapping: fall through and treat as unrecognised
                }
            }
            E e = match(type, raw);
            if (e != null) return e;
            unmapped.computeIfAbsent(field, k -> new LinkedHashSet<>()).add(raw);
            List<String> allowed = new ArrayList<>();
            for (E o : type.getEnumConstants()) allowed.add(o.label());
            error(c.label + ": \"" + raw + "\" is not recognised. Allowed: " + String.join(", ", allowed) + ".");
            return null;
        }

        LocalDateTime occurredAt(LocalDateTime latest) {
            String dateCell = text(Col.DATE_TIME).isEmpty() ? text(Col.DATE) : text(Col.DATE_TIME);
            String timeCell = text(Col.TIME);
            if (dateCell.isEmpty()) {
                error("Date is required.");
                return null;
            }
            String datePart = dateCell;
            String timePart = timeCell;
            if (timePart.isEmpty()) {
                // "2026-03-05 14:30" or "2026-03-05T14:30" in a single cell
                int cut = indexOfTimeSplit(dateCell);
                if (cut > 0) {
                    datePart = dateCell.substring(0, cut).trim();
                    timePart = dateCell.substring(cut + 1).trim();
                }
            }
            LocalDate d = parseDate(datePart);
            if (d == null) {
                error("Date: \"" + datePart + "\" is not a date I can read. Use 2026-03-05 or 05/03/2026 (day first).");
            }
            if (timePart.isEmpty()) {
                error("Time is required (for example 14:30).");
                return null;
            }
            LocalTime t = parseTime(timePart);
            if (t == null) {
                error("Time: \"" + timePart + "\" is not a time I can read. Use 14:30 or 2:30 PM.");
            }
            if (d == null || t == null) return null;
            LocalDateTime dt = LocalDateTime.of(d, t);
            if (dt.isAfter(latest)) {
                error("The incident date and time are in the future.");
                return null;
            }
            if (d.getYear() < 2000) {
                error("The incident date looks wrong (year " + d.getYear() + ").");
                return null;
            }
            return dt;
        }
    }

    private static int indexOfTimeSplit(String s) {
        int t = s.indexOf('T');
        if (t > 0) return t;
        return s.indexOf(' ');
    }

    // ---------------------------------------------------------------- values

    private static final Map<Class<?>, Map<String, Enum<?>>> LOOKUPS = new java.util.concurrent.ConcurrentHashMap<>();

    /** Matches an enum by its name or its on-screen label, ignoring case, spaces and punctuation. */
    static <E extends Enum<E> & Labeled> E match(Class<E> type, String raw) {
        @SuppressWarnings("unchecked")
        Map<String, Enum<?>> map = LOOKUPS.computeIfAbsent(type, t -> {
            Map<String, Enum<?>> m = new HashMap<>();
            for (E e : type.getEnumConstants()) {
                m.put(norm(e.name()), e);
                m.put(norm(e.label()), e);
            }
            if ((Class<?>) type == Frequency.class) {
                m.put("1", Frequency.ONE);
                m.put("once", Frequency.ONE);
                m.put("2", Frequency.TWO);
                m.put("twice", Frequency.TWO);
                m.put("35", Frequency.THREE_TO_FIVE);
                m.put("3to5", Frequency.THREE_TO_FIVE);
                m.put("morethan5", Frequency.MORE_THAN_FIVE);
                m.put("5", Frequency.THREE_TO_FIVE);
                m.put("6", Frequency.MORE_THAN_FIVE);
                m.put("5plus", Frequency.MORE_THAN_FIVE);
            }
            if ((Class<?>) type == Severity.class) {
                m.put("med", Severity.MODERATE);   // common shorthand
                m.put("medium", Severity.MODERATE);
            }
            return m;
        });
        @SuppressWarnings("unchecked")
        E e = (E) map.get(norm(raw));
        return e;
    }

    static String norm(String s) {
        if (s == null) return "";
        StringBuilder b = new StringBuilder();
        for (char ch : s.toLowerCase(Locale.ROOT).toCharArray()) {
            if (Character.isLetterOrDigit(ch)) b.append(ch);
            else if (ch == '+') b.append("plus");
        }
        return b.toString();
    }

    private static Map<String, Map<String, String>> normaliseMappings(Map<String, Map<String, String>> in) {
        Map<String, Map<String, String>> out = new HashMap<>();
        if (in == null) return out;
        in.forEach((field, m) -> {
            if (m == null) return;
            Map<String, String> inner = new HashMap<>();
            m.forEach((raw, target) -> {
                if (raw != null && target != null) inner.put(norm(raw), target);
            });
            out.put(field, inner);
        });
        return out;
    }

    private static final List<DateTimeFormatter> DATE_FORMATS = new ArrayList<>();
    private static final List<DateTimeFormatter> TIME_FORMATS = new ArrayList<>();

    static {
        for (String p : new String[]{"uuuu-M-d", "d/M/uuuu", "d-M-uuuu", "d.M.uuuu", "d/M/uu", "d MMM uuuu", "d MMMM uuuu", "d-MMM-uuuu", "uuuu/M/d"}) {
            DATE_FORMATS.add(DateTimeFormatter.ofPattern(p, Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT));
        }
        for (String p : new String[]{"H:mm", "H:mm:ss", "h:mm a", "h:mm:ss a", "h a", "H.mm"}) {
            TIME_FORMATS.add(DateTimeFormatter.ofPattern(p, Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT));
        }
    }

    static LocalDate parseDate(String s) {
        String v = s.trim();
        for (DateTimeFormatter f : DATE_FORMATS) {
            try {
                return LocalDate.parse(v, f);
            } catch (DateTimeParseException ignored) {
                // try the next format
            }
        }
        return null;
    }

    static LocalTime parseTime(String s) {
        String v = s.trim().toUpperCase(Locale.ENGLISH).replace(".", ":").replaceAll("\\s*([AP])\\:?M\\:?$", " $1M");
        if (v.matches("\\d{1,2}:\\d{2}:\\d{2}\\.\\d+")) v = v.substring(0, v.indexOf('.'));
        for (DateTimeFormatter f : TIME_FORMATS) {
            try {
                return LocalTime.parse(v, f);
            } catch (DateTimeParseException ignored) {
                // try the next format
            }
        }
        return null;
    }

    // ---------------------------------------------------------------- CSV reading

    /** UTF-8 (with or without a BOM); falls back to Windows-1252 for files saved by older Excel versions. */
    static String decode(byte[] bytes) {
        int start = (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF) ? 3 : 0;
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes, start, bytes.length - start)).toString();
        } catch (CharacterCodingException e) {
            return new String(bytes, start, bytes.length - start, Charset.forName("windows-1252"));
        }
    }

    /** Reads RFC 4180 CSV: quoted cells, doubled quotes and line breaks inside quotes. Comma, semicolon or tab separated. */
    static List<List<String>> readCsv(String text) {
        char delim = detectDelimiter(text);
        List<List<String>> out = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        boolean wasQuoted = false;
        int n = text.length();
        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < n && text.charAt(i + 1) == '"') {
                        cell.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    cell.append(c);
                }
            } else if (c == '"' && cell.length() == 0) {
                quoted = true;
                wasQuoted = true;
            } else if (c == delim) {
                row.add(cell.toString());
                cell.setLength(0);
                wasQuoted = false;
            } else if (c == '\n' || c == '\r') {
                if (c == '\r' && i + 1 < n && text.charAt(i + 1) == '\n') i++;
                row.add(cell.toString());
                cell.setLength(0);
                addRow(out, row, wasQuoted);
                row = new ArrayList<>();
                wasQuoted = false;
            } else {
                cell.append(c);
            }
        }
        if (cell.length() > 0 || !row.isEmpty()) {
            row.add(cell.toString());
            addRow(out, row, wasQuoted);
        }
        return out;
    }

    private static void addRow(List<List<String>> out, List<String> row, boolean wasQuoted) {
        boolean blank = true;
        for (String c : row) {
            if (!c.trim().isEmpty()) {
                blank = false;
                break;
            }
        }
        out.add(blank ? List.of() : row);   // blank lines are kept so row numbers match the spreadsheet
    }

    private static boolean isBlank(List<String> row) {
        return row.isEmpty();
    }

    private static char detectDelimiter(String text) {
        int comma = 0, semi = 0, tab = 0;
        boolean quoted = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') quoted = !quoted;
            else if (!quoted) {
                if (c == '\n' || c == '\r') break;
                if (c == ',') comma++;
                else if (c == ';') semi++;
                else if (c == '\t') tab++;
            }
        }
        if (tab > comma && tab > semi) return '\t';
        return semi > comma ? ';' : ',';
    }
}
