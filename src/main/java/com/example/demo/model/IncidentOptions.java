package com.example.demo.model;

import java.util.*;

/** Every choice offered on the incident report form. Only the description is free text. */
public final class IncidentOptions {

    private IncidentOptions() {
    }

    public interface Labeled {
        String label();
    }

    public record Option(String value, String label) {
    }

    public enum Location implements Labeled {
        HOME("Home"),
        COMMUNITY("Community"),
        VEHICLE("Vehicle / transport"),
        OTHER("Other");

        private final String label;

        Location(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Antecedent implements Labeled {
        ENVIRONMENTAL_SITUATIONAL("Environmental / Situational Factors"),
        INTERNAL_PERSONAL("Internal / Personal Factors"),
        ROUTINE_TIME_BASED("Routine / Time-Based Triggers"),
        SOCIAL_INTERACTIONAL("Social / Interactional Factors"),
        TASK_ACTIVITY("Task / Activity Related Factors"),
        OTHER("Other");

        private final String label;

        Antecedent(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum InternalFactor implements Labeled {
        FEELING_OVERWHELMED("Feeling overwhelmed / overstimulated"),
        HORMONAL_CHANGES("Hormonal changes / menstruation"),
        HUNGER_OR_THIRST("Hunger or thirst"),
        MEDICATION_SIDE_EFFECTS("Medication side effects or changes"),
        MENTAL_HEALTH_DETERIORATION("Mental health deterioration"),
        POOR_SLEEP_FATIGUE("Poor sleep / fatigue"),
        SENSORY_DEPRIVATION("Sensory deprivation (not enough stimulation)"),
        SENSORY_OVERLOAD("Sensory overload (noise, lights, smell, touch)"),
        UNWELL_PHYSICAL_PAIN("Unwell / physical pain"),
        OTHER("Other");

        private final String label;

        InternalFactor(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Behaviour implements Labeled {
        ABSCONDING_WANDERING("Absconding/Wandering"),
        FOOD_RELATED("Food Related Behaviour"),
        HARM_TO_SELF("Harm to self"),
        HARMFUL_ACTIVITIES("Harmful Activities"),
        NON_COMPLIANCE_REFUSAL("Non-compliance/Refusal"),
        PHYSICAL_AGGRESSION("Physical Aggression"),
        PROPERTY_DAMAGE_DESTRUCTION("Property Damage/Destruction"),
        RISK_TAKING("Risk taking behaviours"),
        SEXUALISED("Sexualised Behaviour"),
        SOCIALLY_INAPPROPRIATE("Socially Inappropriate Behaviours"),
        SUBSTANCE_MISUSE("Substance Misuse"),
        THREATENING("Threatening Behaviour"),
        VERBAL_AGGRESSION("Verbal Aggression"),
        WITHDRAWAL_ISOLATION("Withdrawal/Isolation"),
        OTHER("Other");

        private final String label;

        Behaviour(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum HarmfulActivity implements Labeled {
        CRIMINAL_ILLEGAL_ACTIVITIES("Engagement in criminal/illegal activities"),
        RISKY_SITUATIONS("Engagement in risky situations"),
        EXCESSIVE_ALCOHOL("Excessive consumption of alcohol"),
        ILLICIT_DRUG_USE("Illicit drug use and paraphernalia"),
        PUTTING_SELF_IN_HARM("Putting self into harmful situations"),
        STEALING_FOOD("Stealing food items"),
        STEALING_MONEY("Stealing money"),
        STEALING_NON_FOOD("Stealing non-food items"),
        USE_OF_WEAPONS("Use of weapons"),
        OTHER("Other");

        private final String label;

        HarmfulActivity(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Consequence implements Labeled {
        ACTIVITY_OR_OUTING_CANCELLED("Activity or outing cancelled"),
        DAMAGE_TO_PERSONAL_ITEMS_OR_ENVIRONMENT("Damage to personal items or environment"),
        EMOTIONAL_DISTRESS_TO_OTHERS("Emotional distress to others"),
        EMOTIONAL_DISTRESS_TO_SELF("Emotional distress to self"),
        IMPACTED_PEERS_OR_CO_RESIDENTS("Impacted peers or co-residents"),
        INCREASED_ANXIETY_OR_AGITATION("Increased anxiety or agitation post-incident"),
        INCREASED_SUPERVISION("Increased supervision temporarily"),
        INJURY_TO_OTHER_PEOPLE("Injury to other people"),
        INJURY_TO_SELF("Injury to self"),
        LOSS_OR_BREAKAGE_OF_BELONGINGS("Loss or breakage of personal belongings"),
        PROPERTY_DAMAGE_DESTRUCTION("Property damage/destruction"),
        REFUSAL_OF_FOOD_MEDICATION_ACTIVITY("Refusal of food/medication/activity"),
        REFUSED_COMMUNICATION_DEBRIEF("Refused communication/debrief"),
        REQUIRED_ENVIRONMENTAL_CHANGE("Required environmental change (e.g., moved to safe space)"),
        REQUIRED_PHYSICAL_INTERVENTION("Required physical intervention (authorised RP only)"),
        REQUIRED_POLICE_AMBULANCE("Required police/ambulance intervention"),
        REQUIRED_PRN_MEDICATION("Required PRN medication"),
        WITHDRAWAL_OR_ISOLATION("Withdrawal or isolation from activity"),
        OTHER("Other");

        private final String label;

        Consequence(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum PersonInvolved implements Labeled {
        FAMILY_MEMBER("Family member"),
        FRIEND("Friend"),
        HEALTH_PROFESSIONAL("Health professional"),
        HOUSEMATE("Housemate"),
        NEIGHBOUR("Neighbour"),
        PARTNER_EX_PARTNER("Partner / Ex-partner"),
        SOMEONE_FROM_COMMUNITY("Someone from the community"),
        STAFF_MEMBER("Staff member"),
        OTHER("Other");

        private final String label;

        PersonInvolved(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Duration implements Labeled {
        UNDER_5_MINS("Under 5 mins"),
        MINS_5_TO_10("5-10 mins"),
        MINS_10_TO_30("10-30 mins"),
        MINS_30_TO_60("30-60 mins"),
        HOURS_1_TO_2("1-2 hours"),
        OVER_2_HOURS("Over 2 hours");

        private final String label;

        Duration(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Frequency implements Labeled {
        ONE("One"),
        TWO("Two"),
        THREE_TO_FIVE("Three to five"),
        MORE_THAN_FIVE("More than five");

        private final String label;

        Frequency(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Severity implements Labeled {
        LOW("Low"),
        MILD("Mild"),
        MODERATE("Moderate"),
        HIGH("High"),
        SEVERE("Severe");

        private final String label;

        Severity(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum EmergencyService implements Labeled {
        AMBULANCE("Ambulance"),
        MENTAL_HEALTH_SERVICES("Mental health services"),
        NONE_INVOLVED("None involved"),
        POLICE_SAPOL("Police (SAPOL)"),
        OTHER("Other");

        private final String label;

        EmergencyService(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Strategy implements Labeled {
        BEHAVIOUR_TEACHING_REINFORCEMENT("Behaviour Teaching & Reinforcement"),
        COMMUNICATION_EMOTIONAL_SUPPORT("Communication & Emotional Support"),
        ENVIRONMENTAL_SAFETY_ADJUSTMENTS("Environmental / Safety Adjustments"),
        MEDICAL_PROCEDURAL("Medical & Procedural"),
        REGULATION_DE_ESCALATION("Regulation & De-Escalation"),
        OTHER("Other");

        private final String label;

        Strategy(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum CommunicationSupport implements Labeled {
        DEBRIEF_AFTER_CRISIS("Debrief conducted after crisis"),
        PROMPTED_TO_IDENTIFY_EMOTIONS("Prompted to identify and communicate emotions"),
        VERBAL_PRAISE("Provided verbal praise for calm or positive choices"),
        BOUNDARIES_AND_EXPECTATIONS("Set up solid boundaries and clear expectations"),
        PROBLEM_SOLVING_DISCUSSION("Supported with problem-solving discussion"),
        CALM_REASSURING_TONE("Used calm, reassuring tone"),
        VISUAL_AIDS_SOCIAL_STORIES("Used visual aids or social stories to support understanding"),
        VISUAL_REMINDER_CUE("Visual reminder or cue provided"),
        OTHER("Other");

        private final String label;

        CommunicationSupport(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum FollowUpAgency implements Labeled {
        BEHAVIOUR_SUPPORT_PRACTITIONER("Behaviour Support Practitioner"),
        SUPPORT_COORDINATOR("Support Coordinator"),
        POLICE_EMERGENCY_SERVICES("Police / Emergency Services"),
        FAMILY_MEMBERS("Family members"),
        OTHER_AGENCY("Other person or agency"),
        NONE("Not referred");

        private final String label;

        FollowUpAgency(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    private static <E extends Enum<E> & Labeled> List<Option> of(Class<E> type) {
        return Arrays.stream(type.getEnumConstants()).map(e -> new Option(e.name(), e.label())).toList();
    }

    /** All option lists keyed by form field name, so a UI can render the radio buttons and checkboxes. */
    public static Map<String, List<Option>> all() {
        Map<String, List<Option>> m = new LinkedHashMap<>();
        m.put("location", of(Location.class));
        m.put("antecedents", of(Antecedent.class));
        m.put("internalFactors", of(InternalFactor.class));
        m.put("behaviours", of(Behaviour.class));
        m.put("harmfulActivities", of(HarmfulActivity.class));
        m.put("consequences", of(Consequence.class));
        m.put("peopleInvolved", of(PersonInvolved.class));
        m.put("duration", of(Duration.class));
        m.put("frequency", of(Frequency.class));
        m.put("severity", of(Severity.class));
        m.put("emergencyServices", of(EmergencyService.class));
        m.put("strategies", of(Strategy.class));
        m.put("communicationSupports", of(CommunicationSupport.class));
        m.put("followUpAgencies", of(FollowUpAgency.class));
        return m;
    }
}
