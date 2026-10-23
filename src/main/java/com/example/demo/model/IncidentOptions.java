package com.example.demo.model;

import java.util.*;

/** Every choice offered on the incident report form. Only the description is free text. */
public final class IncidentOptions {

    private IncidentOptions() {
    }

    public interface Labeled {
        String label();

        /** The category this choice is shown under, or null when the list has no categories. */
        default String group() {
            return null;
        }
    }

    public record Option(String value, String label, String group) {
        public Option(String value, String label) {
            this(value, label, null);
        }
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
        // Access or Activity Related Factors
        ACCESS_DENIAL_OF_A_PREFERRED_ITEM_OR_ACTIVITY("Denial of a preferred item or activity", "Access or Activity Related Factors"),
        ACCESS_DELAYED_ACCESS_TO_A_PREFERRED_ITEM_OR_ACTIVITY("Delayed access to a preferred item or activity", "Access or Activity Related Factors"),
        ACCESS_EXPOSURE_TO_A_PREFERRED_ITEM_OR_ACTIVITY_IN_THE("Exposure to a preferred item or activity in the community", "Access or Activity Related Factors"),
        ACCESS_REMOVAL_OF_A_PREFERRED_ITEM_OR_ACTIVITY("Removal of a preferred item or activity", "Access or Activity Related Factors"),
        ACCESS_INTERRUPTION_OF_AN_ONGOING_ACTIVITY("Interruption of an ongoing activity", "Access or Activity Related Factors"),
        ACCESS_BEING_ASKED_TO_SHARE_OR_RELINQUISH_A_PREFERRED("Being asked to share or relinquish a preferred item", "Access or Activity Related Factors"),
        ACCESS_UNAVAILABILITY_OF_AN_EXPECTED_ITEM_OR_ACTIVITY("Unavailability of an expected item or activity", "Access or Activity Related Factors"),
        ACCESS_BEING_PREVENTED_FROM_COMPLETING_A_PREFERRED("Being prevented from completing a preferred activity", "Access or Activity Related Factors"),
        ACCESS_EXPERIENCING_FAILURE_OR_LOSS_DURING_A_PREFERRED("Experiencing failure or loss during a preferred activity", "Access or Activity Related Factors"),
        ACCESS_TRANSITIONING_FROM_A_PREFERRED_ACTIVITY("Transitioning from a preferred activity", "Access or Activity Related Factors"),
        // Social or Interactional Factors
        SOCIAL_CONFLICT_OR_DISAGREEMENT_WITH_OTHERS("Conflict or disagreement with others", "Social or Interactional Factors"),
        SOCIAL_UNABLE_TO_RECEIVE_ATTENTION_FROM_OTHERS("Unable to receive attention from others", "Social or Interactional Factors"),
        SOCIAL_PEER_PRESSURE_OR_INFLUENCE_FROM_OTHERS("Peer pressure or influence from others", "Social or Interactional Factors"),
        SOCIAL_INTERACTION_WITH_UNFAMILIAR_PERSON("Interaction with unfamiliar person", "Social or Interactional Factors"),
        SOCIAL_STAFF_CHANGE_OR_TRANSITION("Staff change or transition", "Social or Interactional Factors"),
        SOCIAL_SOCIAL_REJECTION_OR_EXCLUSION("Social rejection or exclusion", "Social or Interactional Factors"),
        SOCIAL_SOCIAL_EXHAUSTION_OR_FATIGUE("Social exhaustion or fatigue", "Social or Interactional Factors"),
        SOCIAL_PREFERRED_PERSON_LEAVES("Preferred person leaves", "Social or Interactional Factors"),
        SOCIAL_PREFERRED_PERSON_UNAVAILABLE_WHEN_REQUESTED("Preferred person unavailable when requested", "Social or Interactional Factors"),
        SOCIAL_PROVIDED_SOCIAL_DEMANDS("Provided social demands (i.e. questions or feedback)", "Social or Interactional Factors"),
        SOCIAL_COMMUNICATION_ATTEMPTS_NOT_UNDERSTOOD("Communication attempts not understood", "Social or Interactional Factors"),
        // Instructional or Demand Related Factors
        DEMAND_ASKED_TO_COMPLETE_CHORE_ROUTINE_OR_DAILY_LIVING("Asked to complete chore, routine, or daily living tasks", "Instructional or Demand Related Factors"),
        DEMAND_ASKED_TO_COMPLETE_PERSONAL_CARE_ACTIVITIES("Asked to complete personal care activities (i.e. showering)", "Instructional or Demand Related Factors"),
        DEMAND_PROVIDED_WITH_AN_INSTRUCTION_DEMAND_OR_REQUEST("Provided with an instruction, demand, or request", "Instructional or Demand Related Factors"),
        DEMAND_REPEATED_PROMPTING_OR_CORRECTIONS("Repeated prompting or corrections", "Instructional or Demand Related Factors"),
        DEMAND_ENGAGEMENT_IN_A_NON_PREFERRED_ACTIVITY("Engagement in a non-preferred activity", "Instructional or Demand Related Factors"),
        DEMAND_TASK_TOO_DIFFICULT_COMPLEX_OR_UNFAMILIAR("Task too difficult, complex, or unfamiliar", "Instructional or Demand Related Factors"),
        DEMAND_TOO_MANY_INSTRUCTIONS_GIVEN("Too many instructions given", "Instructional or Demand Related Factors"),
        DEMAND_OVERSTIMULATION_DUE_TO_PROLONGED_TASK_DURATION("Overstimulation due to prolonged task duration", "Instructional or Demand Related Factors"),
        DEMAND_ASKED_TO_TRANSITION_TO_NEW_ACTIVITY_OR_TASK("Asked to transition to new activity or task", "Instructional or Demand Related Factors"),
        DEMAND_UNEXPECTED_CHANGE_IN_ROUTINE_OR_SCHEDULE("Unexpected change in routine or schedule", "Instructional or Demand Related Factors"),
        DEMAND_LOSS_OF_AUTONOMY_OR_CHOICE("Loss of autonomy or choice", "Instructional or Demand Related Factors"),
        DEMAND_ANTICIPATION_OF_AN_UPCOMING_APPOINTMENT_TASK_OR("Anticipation of an upcoming appointment, task, or demand", "Instructional or Demand Related Factors"),
        // Physiological and Sensory-related Factors
        SENSORY_SENSORY_OVERLOAD_OR_OVERSTIMULATION("Sensory overload or overstimulation", "Physiological and Sensory-related Factors"),
        SENSORY_REDUCED_SENSORY_STIMULATION_OR_BOREDOM("Reduced sensory stimulation or boredom", "Physiological and Sensory-related Factors"),
        SENSORY_UNEXPECTED_OR_SUDDEN_LOUD_NOISE("Unexpected or sudden loud noise", "Physiological and Sensory-related Factors"),
        SENSORY_LOUD_NOISY_OR_CROWDED_ENVIRONMENT("Loud, noisy, or crowded environment", "Physiological and Sensory-related Factors"),
        SENSORY_BRIGHT_LIGHTS_OR_EXCESSIVE_VISUAL_STIMULATION("Bright lights or excessive visual stimulation", "Physiological and Sensory-related Factors"),
        SENSORY_UNWANTED_TOUCH_OR_TACTILE_STIMULATION("Unwanted touch or tactile stimulation (i.e. rain, sand, or particular textures)", "Physiological and Sensory-related Factors"),
        SENSORY_DISCOMFORT_WITH_STRONG_SMELLS_IN_THE_ENVIRONMENT("Discomfort with strong smells in the environment", "Physiological and Sensory-related Factors"),
        SENSORY_DISCOMFORT_WITH_TEMPERATURE("Discomfort with temperature", "Physiological and Sensory-related Factors"),
        SENSORY_UNWANTED_PROXIMITY_OR_INTRUSION_INTO_PERSONAL("Unwanted proximity or intrusion into personal space", "Physiological and Sensory-related Factors"),
        SENSORY_UNFAMILIAR_OR_NEW_ENVIRONMENT("Unfamiliar or new environment", "Physiological and Sensory-related Factors"),
        SENSORY_UNEXPECTED_CHANGE_TO_PHYSICAL_ENVIRONMENT("Unexpected change to physical environment", "Physiological and Sensory-related Factors"),
        SENSORY_MOVEMENT_RESTRICTED("Movement restricted (i.e. seatbelts)", "Physiological and Sensory-related Factors"),
        SENSORY_NEED_FOR_ORAL_STIMULATION("Need for oral stimulation (i.e. chewing)", "Physiological and Sensory-related Factors"),
        SENSORY_HUNGER_OR_THIRST("Hunger or thirst", "Physiological and Sensory-related Factors"),
        SENSORY_ILLNESS_PHYSICAL_PAIN_OR_DISCOMFORT("Illness, physical pain, or discomfort", "Physiological and Sensory-related Factors"),
        SENSORY_FATIGUE_OR_INSUFFICIENT_SLEEP("Fatigue or insufficient sleep", "Physiological and Sensory-related Factors"),
        SENSORY_HORMONAL_CHANGES_OR_MENSTRUATION("Hormonal changes or menstruation", "Physiological and Sensory-related Factors"),
        SENSORY_SEXUAL_AROUSAL("Sexual arousal", "Physiological and Sensory-related Factors"),
        SENSORY_TOILETING_NEEDS("Toileting needs", "Physiological and Sensory-related Factors"),
        // Mental Health and Emotional Factors
        MENTAL_REPORTED_OR_OBSERVED_CHANGE_IN_MENTAL_HEALTH("Reported or observed change in mental health presentation", "Mental Health and Emotional Factors"),
        MENTAL_DISTRESS_ASSOCIATED_WITH_HALLUCINATIONS_OR("Distress associated with hallucinations or delusions", "Mental Health and Emotional Factors"),
        MENTAL_REPORTED_MEDICATION_EFFECTS_OR_CHANGES_IN("Reported medication effects or changes in symptoms following medication changes", "Mental Health and Emotional Factors"),
        MENTAL_RECENT_ALCOHOL_OR_OTHER_DRUG_USE("Recent alcohol or other drug use", "Mental Health and Emotional Factors"),
        MENTAL_DISTRESS_FOLLOWING_UPSETTING_EVENTS_OR_REMINDERS("Distress following upsetting events or reminders of past trauma", "Mental Health and Emotional Factors"),
        MENTAL_DIFFICULTY_COPING_WITH_DISTRESS_OR_UNCERTAINTY("Difficulty coping with distress or uncertainty", "Mental Health and Emotional Factors"),
        MENTAL_OBSERVABLE_OR_REPORTED_ANXIETY_WORRY_OR_LOW_MOOD("Observable or reported anxiety, worry, or low mood", "Mental Health and Emotional Factors"),
        MENTAL_REPORTED_FEAR_PERCEIVED_THREAT_OR_FEELING_UNSAFE("Reported fear, perceived threat, or feeling unsafe", "Mental Health and Emotional Factors"),
        // Other or Unknown Factor
        UNKNOWN_OTHER("Other", "Other or Unknown Factor"),
        // Earlier choices, kept so existing records still load. Not offered on the form.
        ENVIRONMENTAL_SITUATIONAL("Environmental / Situational Factors"),
        INTERNAL_PERSONAL("Internal / Personal Factors"),
        ROUTINE_TIME_BASED("Routine / Time-Based Triggers"),
        SOCIAL_INTERACTIONAL("Social / Interactional Factors"),
        TASK_ACTIVITY("Task / Activity Related Factors"),
        OTHER("Other");

        private final String label;
        private final String group;

        Antecedent(String label) {
            this(label, null);
        }

        Antecedent(String label, String group) {
            this.label = label;
            this.group = group;
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public String group() {
            return group;
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
        // Verbal Aggression
        VERBAL_YELLING_SCREAMING("Yelling/screaming", "Verbal Aggression"),
        VERBAL_SWEARING_INSULTING("Swearing/insulting", "Verbal Aggression"),
        VERBAL_ARGUING_DEMANDING("Arguing/demanding", "Verbal Aggression"),
        VERBAL_INTIMIDATING_BODY_LANGUAGE_GESTURES("Intimidating body language/gestures", "Verbal Aggression"),
        VERBAL_NON_VIOLENT_THREATS("Non-violent threats", "Verbal Aggression"),
        VERBAL_THREATS_OF_VIOLENCE("Threats of violence", "Verbal Aggression"),
        VERBAL_LOCKING_DOORS_ON_OTHERS("Locking doors on others", "Verbal Aggression"),
        // Physical Aggression
        PHYSICAL_ATTEMPTS_TO_HARM_OTHERS("Attempts to harm others (i.e. swings fist)", "Physical Aggression"),
        PHYSICAL_HITTING_PUNCHING("Hitting/punching", "Physical Aggression"),
        PHYSICAL_KICKING_KNEEING("Kicking/kneeing", "Physical Aggression"),
        PHYSICAL_PUSHING_OR_PULLING_OTHERS_AWAY("Pushing or pulling others away", "Physical Aggression"),
        PHYSICAL_HOLDING_GRABBING_OTHERS_BODY_OR_CLOTHING("Holding/grabbing others body or clothing", "Physical Aggression"),
        PHYSICAL_HAIR_PULLING("Hair pulling", "Physical Aggression"),
        PHYSICAL_STRANGLING_CHOKING("Strangling/choking", "Physical Aggression"),
        PHYSICAL_SCRATCHING_PINCHING("Scratching/pinching", "Physical Aggression"),
        PHYSICAL_BITING_OTHERS("Biting others", "Physical Aggression"),
        PHYSICAL_SPITTING_ON_OTHERS("Spitting on others", "Physical Aggression"),
        PHYSICAL_THROWING_ITEMS_TOWARDS_PEOPLE("Throwing items towards people", "Physical Aggression"),
        PHYSICAL_USING_OBJECTS_TO_HIT_WHIP_PEOPLE("Using objects to hit/whip people", "Physical Aggression"),
        PHYSICAL_PHYSICAL_AGGRESSION_AGAINST_ANIMALS("Physical aggression against animals", "Physical Aggression"),
        // Property Damage
        PROPERTY_THROWING_ITEMS_TOWARDS_PROPERTY("Throwing items towards property", "Property Damage"),
        PROPERTY_PUNCHING_AND_KICKING_WALLS_FURNITURE("Punching and kicking walls/furniture", "Property Damage"),
        PROPERTY_BREAKING_OR_SMASHING_OBJECTS_I_E_TV_WINDOW("Breaking or smashing objects i.e. TV, window, plates", "Property Damage"),
        PROPERTY_FLIPPING_TABLES_AND_FURNITURE("Flipping tables and furniture", "Property Damage"),
        PROPERTY_STOMPING_ON_FLOORS("Stomping on floors", "Property Damage"),
        PROPERTY_SLAMMING_DOORS("Slamming doors", "Property Damage"),
        PROPERTY_PUNCHING_BANGING_ON_DOORS("Punching/banging on doors", "Property Damage"),
        PROPERTY_URINATING_ON_FLOORS_FURNITURE("Urinating on floors/furniture", "Property Damage"),
        PROPERTY_DEFECATING_ON_FLOORS_WALLS("Defecating on floors/walls", "Property Damage"),
        PROPERTY_SETTING_FIRES("Setting fires", "Property Damage"),
        PROPERTY_LITTERING_OR_CAUSING_CLUTTER("Littering or causing clutter", "Property Damage"),
        // Absconding and Wandering
        ABSCOND_ATTEMPT_TO_ABSCOND_FROM_MOVING_VEHICLE("Attempt to abscond from moving vehicle", "Absconding and Wandering"),
        ABSCOND_ABSCONDS_FROM_STATIONARY_VEHICLE("Absconds from stationary vehicle", "Absconding and Wandering"),
        ABSCOND_UNSAFELY_WANDERS_INTO_TRAFFIC_OR_OTHER_HAZARDOUS("Unsafely wanders into traffic or other hazardous areas", "Absconding and Wandering"),
        ABSCOND_ABSCONDS_OR_RUNS_AWAY_WHILE_IN_COMMUNITY("Absconds or runs away while in community environment", "Absconding and Wandering"),
        ABSCOND_ABSCONDS_FROM_HOME_WITHOUT_SUPPORT("Absconds from home without support", "Absconding and Wandering"),
        ABSCOND_ABSCONDS_FROM_SCHOOL_APPOINTMENT_OR_DAY_PROGRAM("Absconds from school, appointment, or day program", "Absconding and Wandering"),
        // Harm to Self / Suicidal
        SELFHARM_PULLING_HAIR("Pulling hair", "Harm to Self / Suicidal"),
        SELFHARM_SCRATCHING_ARMS_OR_LEGS("Scratching arms or legs", "Harm to Self / Suicidal"),
        SELFHARM_BITING_ONESELF("Biting oneself", "Harm to Self / Suicidal"),
        SELFHARM_CUTTING_OR_STABBING_ONESELF("Cutting or stabbing oneself", "Harm to Self / Suicidal"),
        SELFHARM_BURNING_ONESELF("Burning oneself", "Harm to Self / Suicidal"),
        SELFHARM_HITTING_OR_PUNCHING_SELF("Hitting or punching self", "Harm to Self / Suicidal"),
        SELFHARM_BANGING_HEAD_ON_WALL_FURNITURE("Banging head on wall/furniture", "Harm to Self / Suicidal"),
        SELFHARM_PUNCHES_OR_KICKS_WALLS_TO_INJURE_ONESELF("Punches or kicks walls to injure oneself", "Harm to Self / Suicidal"),
        SELFHARM_THROWS_ONESELF_ON_THE_GROUND_OR_RUNS_INTO_WALLS("Throws oneself on the ground or runs into walls", "Harm to Self / Suicidal"),
        SELFHARM_INFLICTS_MAJOR_DAMAGE_TO_ONESELF_REQUIRING("Inflicts major damage to oneself requiring first aid care", "Harm to Self / Suicidal"),
        SELFHARM_JUMPS_FROM_HIGH_ENOUGH_PLACE_TO_CAUSE_DAMAGE("Jumps from high enough place to cause damage", "Harm to Self / Suicidal"),
        SELFHARM_ATTEMPTS_TO_STRANGLE_CHOKE_OR_SUFFOCATE_ONESELF("Attempts to strangle, choke, or suffocate oneself", "Harm to Self / Suicidal"),
        SELFHARM_ATTEMPTS_TO_INGEST_HAZARDOUS_CHEMICALS_POISONS("Attempts to ingest hazardous chemicals, poisons, or materials", "Harm to Self / Suicidal"),
        SELFHARM_ATTEMPTS_TO_OVERDOSE_USING_MEDICATION("Attempts to overdose using medication", "Harm to Self / Suicidal"),
        SELFHARM_STOCKPILES_MEDICATION_WITH_SUICIDAL_INTENT("Stockpiles medication with suicidal intent", "Harm to Self / Suicidal"),
        SELFHARM_ATTEMPTS_TO_ELECTROCUTE_ONESELF("Attempts to electrocute oneself", "Harm to Self / Suicidal"),
        SELFHARM_ATTEMPTS_TO_JUMP_OR_STAND_IN_FRONT_OF_TRAFFIC("Attempts to jump or stand in front of traffic or trains", "Harm to Self / Suicidal"),
        SELFHARM_WITHDRAWS_OR_ISOLATES_SELF_FROM_OTHERS("Withdraws or isolates self from others", "Harm to Self / Suicidal"),
        SELFHARM_INDICATES_A_DESIRE_TO_SELF_HARM("Indicates a desire to self-harm", "Harm to Self / Suicidal"),
        SELFHARM_HAPHAZARDLY_OR_CASUALLY_TALKS_ABOUT_SUICIDE("Haphazardly or casually talks about suicide", "Harm to Self / Suicidal"),
        SELFHARM_THREATENS_TO_SELF_HARM_OR_COMMIT_SUICIDE("Threatens to self-harm or commit suicide", "Harm to Self / Suicidal"),
        SELFHARM_EXPRESSES_CLEAR_SUICIDAL_IDEATION_OR_THOUGHTS("Expresses clear suicidal ideation or thoughts", "Harm to Self / Suicidal"),
        SELFHARM_MAKES_OR_DESCRIBES_A_PLAN_FOR_SUICIDE("Makes or describes a plan for suicide", "Harm to Self / Suicidal"),
        SELFHARM_MAKES_SUICIDE_ATTEMPTS("Makes suicide attempts", "Harm to Self / Suicidal"),
        // Sexualised Behaviour
        SEXUAL_EXPOSES_GENITALS_TO_OTHERS_IN_A_SHARED_OR_PUBLIC("Exposes genitals to others in a shared or public setting", "Sexualised Behaviour"),
        SEXUAL_COMPLETELY_REMOVES_CLOTHING_AND_REMAINS_NAKED_IN("Completely removes clothing and remains naked in a shared or public setting", "Sexualised Behaviour"),
        SEXUAL_MASTURBATES_IN_A_SHARED_OR_PUBLIC_SETTING("Masturbates in a shared or public setting", "Sexualised Behaviour"),
        SEXUAL_VIEWS_PORNOGRAPHY_IN_A_SHARED_OR_PUBLIC_SETTING("Views pornography in a shared or public setting", "Sexualised Behaviour"),
        SEXUAL_MAKES_INAPPROPRIATE_SEXUAL_COMMENTS_STATEMENTS_OR("Makes inappropriate sexual comments, statements, or jokes", "Sexualised Behaviour"),
        SEXUAL_MAKES_SEXUALLY_HARASSING_COMMENTS_TOWARDS_ANOTHER("Makes sexually harassing comments towards another person", "Sexualised Behaviour"),
        SEXUAL_DIRECTS_SEXUALISED_GESTURES_TOWARDS_OTHERS("Directs sexualised gestures towards others", "Sexualised Behaviour"),
        SEXUAL_INITIATES_UNWANTED_PHYSICAL_AFFECTION_BY("Initiates unwanted physical affection by attempting to hug or sit close to others", "Sexualised Behaviour"),
        SEXUAL_ATTEMPTS_TO_KISS_ANOTHER_PERSON_S_BODY_WITHOUT("Attempts to kiss another person’s body without consent", "Sexualised Behaviour"),
        SEXUAL_MAKES_UNWANTED_SEXUAL_CONTACT_BY_TOUCHING_OR("Makes unwanted sexual contact by touching or groping another person’s body", "Sexualised Behaviour"),
        SEXUAL_TAKES_ANOTHER_PERSON_S_BELONGINGS_FOR_A_SEXUAL("Takes another person’s belongings for a sexual purpose (i.e. clothing)", "Sexualised Behaviour"),
        SEXUAL_PHOTOGRAPHS_OR_RECORDS_ANOTHER_PERSON_WITHOUT("Photographs or records another person without consent for a sexual purpose", "Sexualised Behaviour"),
        SEXUAL_SENDS_UNSOLICITED_NUDE_IMAGES_OR_VIDEOS_OF("Sends unsolicited nude images or videos of oneself to others", "Sexualised Behaviour"),
        SEXUAL_SENDS_UNSOLICITED_PORNOGRAPHIC_IMAGES_OR_VIDEOS("Sends unsolicited pornographic images or videos to others", "Sexualised Behaviour"),
        SEXUAL_MAKES_UNWANTED_SEXUAL_REQUESTS_PROPOSITIONS_OR("Makes unwanted sexual requests, propositions, or advances towards another person", "Sexualised Behaviour"),
        SEXUAL_ATTEMPTS_TO_COERCE_PRESSURE_OR_INTIMIDATE_OTHERS("Attempts to coerce, pressure, or intimidate others for sexual favours", "Sexualised Behaviour"),
        SEXUAL_OFFERS_OR_USES_MONEY_GIFTS_OR_OTHER_BENEFITS_TO("Offers or uses money, gifts, or other benefits to induce another person into sexual activity", "Sexualised Behaviour"),
        SEXUAL_SEXUALISED_BEHAVIOUR_DIRECTED_TOWARDS_A_PERSON("Sexualised behaviour directed towards a person under the age of consent", "Sexualised Behaviour"),
        // Food-related Behaviour
        FOOD_FOOD_BINGING("Food binging", "Food-related Behaviour"),
        FOOD_FOOD_COMPULSIONS_AND_DEMANDS("Food compulsions and demands", "Food-related Behaviour"),
        FOOD_EATING_NON_FOOD_ITEMS("Eating non-food items", "Food-related Behaviour"),
        FOOD_FOOD_REFUSAL_OR_RESTRICTED_FOOD_INTAKE("Food refusal or restricted food intake", "Food-related Behaviour"),
        FOOD_EXCESSIVE_CONSUMPTION_OF_LIQUIDS("Excessive consumption of liquids", "Food-related Behaviour"),
        // Non-compliance
        NONCOMPLY_NOT_PARTICIPATING_IN_ROUTINE_OR_DAILY_LIVING("Not participating in routine or daily living tasks", "Non-compliance"),
        NONCOMPLY_IGNORING_AND_NOT_LISTENING_TO_INSTRUCTIONS("Ignoring and not listening to instructions", "Non-compliance"),
        NONCOMPLY_DECLINES_TO_TAKE_MEDICATION("Declines to take medication", "Non-compliance"),
        NONCOMPLY_RELUCTANTLY_TAKES_MEDICATION_AFTER_REPEATED("Reluctantly takes medication after repeated prompting", "Non-compliance"),
        NONCOMPLY_UNSAFELY_USES_OR_HANDLES_MEDICATION("Unsafely uses or handles medication", "Non-compliance"),
        NONCOMPLY_DECLINING_OR_NOT_ATTENDING_APPOINTMENTS("Declining or not attending appointments", "Non-compliance"),
        NONCOMPLY_DECLINING_OR_REFUSING_PERSONAL_CARE_ACTIVITIES("Declining or refusing personal care activities", "Non-compliance"),
        NONCOMPLY_DIFFICULTY_IN_MAINTAINING_PERSONAL_HYGIENE("Difficulty in maintaining personal hygiene", "Non-compliance"),
        // Harmful Activities and Socially Inappropriate Behaviours
        HARMFUL_TAKING_FOOD_ITEMS_WITHOUT_PERMISSION("Taking food items without permission", "Harmful Activities and Socially Inappropriate Behaviours"),
        HARMFUL_TAKING_MONEY_OR_CREDIT_CARDS_FROM_OTHERS_WITHOUT("Taking money or credit cards from others without permission", "Harmful Activities and Socially Inappropriate Behaviours"),
        HARMFUL_TAKING_PERSONAL_BELONGINGS_FROM_OTHERS_WITHOUT("Taking personal belongings from others without permission", "Harmful Activities and Socially Inappropriate Behaviours"),
        HARMFUL_EXCESSIVE_OR_UNSAFE_CONSUMPTION_OF_ALCOHOL("Excessive or unsafe consumption of alcohol", "Harmful Activities and Socially Inappropriate Behaviours"),
        HARMFUL_USE_OF_ILLICIT_SUBSTANCES_OR_UNSAFE_SUBSTANCE("Use of illicit substances or unsafe substance use", "Harmful Activities and Socially Inappropriate Behaviours"),
        HARMFUL_ILLICIT_SUBSTANCES_AND_PARAPHERNALIA_FOUND_ON("Illicit substances and paraphernalia found on premise", "Harmful Activities and Socially Inappropriate Behaviours"),
        HARMFUL_SEEKING_OR_DEMANDING_FOR_AOD_SUBSTANCES("Seeking or demanding for AOD substances", "Harmful Activities and Socially Inappropriate Behaviours"),
        HARMFUL_EXCESSIVE_OR_UNSAFE_PURCHASE_OF_AOD_SUBSTANCES("Excessive or unsafe purchase of AOD substances", "Harmful Activities and Socially Inappropriate Behaviours"),
        HARMFUL_MAKES_EXCESSIVE_OR_COMPULSIVE_PURCHASES("Makes excessive or compulsive purchases", "Harmful Activities and Socially Inappropriate Behaviours"),
        HARMFUL_PUTS_SELF_INTO_HARMFUL_OR_UNSAFE_SITUATION("Puts self into harmful or unsafe situation", "Harmful Activities and Socially Inappropriate Behaviours"),
        HARMFUL_ENGAGEMENT_IN_CRIMINAL_ILLEGAL_ACTIVITIES("Engagement in criminal/illegal activities", "Harmful Activities and Socially Inappropriate Behaviours"),
        // Other Behaviour
        OTHERB_OTHER("Other", "Other Behaviour"),
        // Earlier choices, kept so existing records still load. Not offered on the form.
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
        private final String group;

        Behaviour(String label) {
            this(label, null);
        }

        Behaviour(String label, String group) {
            this.label = label;
            this.group = group;
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public String group() {
            return group;
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
        return Arrays.stream(type.getEnumConstants()).map(e -> new Option(e.name(), e.label(), e.group())).toList();
    }

    /** Only the choices the form offers today: everything except the earlier ones kept for existing records. */
    private static <E extends Enum<E> & Labeled> List<Option> ofDocumentChoices(Class<E> type, Set<String> legacyNames) {
        return Arrays.stream(type.getEnumConstants())
                .filter(e -> !legacyNames.contains(e.name()))
                .map(e -> new Option(e.name(), e.label(), e.group()))
                .toList();
    }

    /** The earlier choices, so a report that used them can still be shown and saved. */
    private static <E extends Enum<E> & Labeled> List<Option> ofLegacyChoices(Class<E> type, Set<String> legacyNames) {
        return Arrays.stream(type.getEnumConstants())
                .filter(e -> legacyNames.contains(e.name()))
                .map(e -> new Option(e.name(), e.label()))
                .toList();
    }

    private static final Set<String> LEGACY_ANTECEDENTS = Set.of("ENVIRONMENTAL_SITUATIONAL", "INTERNAL_PERSONAL", "OTHER", "ROUTINE_TIME_BASED", "SOCIAL_INTERACTIONAL", "TASK_ACTIVITY");
    private static final Set<String> LEGACY_BEHAVIOURS = Set.of("ABSCONDING_WANDERING", "FOOD_RELATED", "HARMFUL_ACTIVITIES", "HARM_TO_SELF", "NON_COMPLIANCE_REFUSAL", "OTHER", "PHYSICAL_AGGRESSION", "PROPERTY_DAMAGE_DESTRUCTION", "RISK_TAKING", "SEXUALISED", "SOCIALLY_INAPPROPRIATE", "SUBSTANCE_MISUSE", "THREATENING", "VERBAL_AGGRESSION", "WITHDRAWAL_ISOLATION");

    /** All option lists keyed by form field name, so a UI can render the radio buttons and checkboxes. */
    public static Map<String, List<Option>> all() {
        Map<String, List<Option>> m = new LinkedHashMap<>();
        m.put("location", of(Location.class));
        m.put("antecedents", ofDocumentChoices(Antecedent.class, LEGACY_ANTECEDENTS));
        m.put("antecedentsLegacy", ofLegacyChoices(Antecedent.class, LEGACY_ANTECEDENTS));
        m.put("internalFactors", of(InternalFactor.class));
        m.put("behaviours", ofDocumentChoices(Behaviour.class, LEGACY_BEHAVIOURS));
        m.put("behavioursLegacy", ofLegacyChoices(Behaviour.class, LEGACY_BEHAVIOURS));
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