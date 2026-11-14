workspace "Noxway Platform Solution - C4 Model" "System-wide software architecture for Noxway night-shift safety, community map, wellness, and companion monitoring platform." {

    model {
        // Actors / Personas
        worker = person "Night-Shift Worker" "Starts monitored commutes, sends GPS telemetry pings, triggers SOS incidents, logs sleep sessions, and shares 24h services & notices."
        companion = person "Trusted Contact (Companion)" "Monitors linked workers in real time, receives multi-channel alerts, and resolves emergency incidents."
        admin = person "Moderator / Administrator" "Approves or rejects community service points and risk zones, and reconciles offline subscription payments."

        // External Systems
        osmSystem = softwareSystem "OpenStreetMap & OpenFreeMap" "Provides interactive street raster tiles and metropolitan vector tiles for 24h pharmacies, fuel stations, supermarkets, hospitals, and police stations." {
            tags "External System"
        }

        emailProvider = softwareSystem "SMTP / Alert Delivery Provider" "Delivers outbound email, SMS, and WhatsApp emergency notifications to trusted contacts." {
            tags "External System"
        }

        offlinePaymentNetwork = softwareSystem "Offline Payment Channels (Yape / Plin / Agent)" "Generates payment receipt references validated by platform administrators." {
            tags "External System"
        }

        // Solution Scope
        solution = softwareSystem "Noxway Ecosystem" "Comprehensive safety, community 24h map, sleep recovery, and companion monitoring platform for shift workers." {

            singlePageApp = container "Noxway Web Application (SPA)" "Delivers the Worker Portal, 24h OpenStreetMap Explorer, Community Notice Board, and Companion Live Portal." "Angular 21, TypeScript, Leaflet, pbf" {
                tags "Web Browser"
            }

            noxwayApi = container "Noxway Platform REST API" "Provides RESTful endpoints (/api/v1) across 7 bounded contexts with CQRS, ACL facades, optimistic locking, and RFC 9457 Problem Details." "Java 21, Spring Boot 3.5.7" {
                tags "Server-side Application"

                iamModule = component "IAM Module" "Actor context resolution (X-Actor-Id) and structured authentication contracts (/api/v1/auth/*)." "Spring Module"
                profilesModule = component "Profiles & Trust Network Module" "Worker/contact profiles, directory, notification preferences, and TrustLink lifecycle." "Spring Module"
                commutingModule = component "Commuting & Companion Module" "Active commutes, GPS telemetry pings, delays, SOS incidents, companion live feed, and alert delivery queue." "Spring Module"
                communityModule = component "Community & 24h Map Module" "24/7 service points, opening intervals, trust voting, risk zones, route ratings, and geo-tagged community notices." "Spring Module"
                wellnessModule = component "Wellness & Sleep Module" "Rest session logging, overlap validation, daily sleep goals, and 7-day timezone-aware recovery summaries." "Spring Module"
                subscriptionsModule = component "Subscriptions & Benefits Module" "Plans (ESSENTIAL, SENTINEL_PRO, FAMILY_CREW), offline payment reconciliation, collective benefits, family crew, and referrals." "Spring Module"
                moderationModule = component "Moderation Module" "Review queue for community service points and risk zones with immutable audit trail." "Spring Module"
                sharedModule = component "Shared Kernel" "AbstractDomainAggregateRoot, Result<T,E>, OptimisticJpaSaveSupport, and RFC 9457 ProblemDetail i18n assemblers." "Spring Module"
            }

            database = container "Noxway Relational Database" "Stores 29 tables across 6 isolated domain schemas managed by Flyway migrations V1-V5." "PostgreSQL 16 / H2 (PostgreSQL Mode)" {
                tags "Database"
            }
        }

        // Actor interactions
        worker -> singlePageApp "Manages commutes, explores 24h map, publishes notices, logs sleep, and claims benefits" "HTTPS"
        companion -> singlePageApp "Monitors active worker commutes and responds to SOS alerts in Companion Portal" "HTTPS"
        admin -> singlePageApp "Moderates community reports and confirms offline subscription payments" "HTTPS"

        // SPA integrations
        singlePageApp -> noxwayApi "Consumes REST endpoints with X-Actor-Id and Accept-Language headers" "JSON/HTTPS"
        singlePageApp -> osmSystem "Fetches raster map tiles and OpenFreeMap PBF vector tiles for nearby POIs" "HTTPS"
        worker -> offlinePaymentNetwork "Transfers subscription payment and obtains receipt reference" "Mobile Banking"

        // API integrations
        noxwayApi -> database "Reads and writes across 6 schemas via Spring Data JPA & Flyway" "JDBC"
        noxwayApi -> emailProvider "Dispatches queued alert notifications" "SMTP/HTTPS"

        // Internal Component Relationships (ACL & Shared Kernel)
        iamModule -> sharedModule "Uses shared error handling and actor context"
        profilesModule -> sharedModule "Uses shared abstractions and OptimisticJpaSaveSupport"
        commutingModule -> sharedModule "Uses shared abstractions and OptimisticJpaSaveSupport"
        communityModule -> sharedModule "Uses shared abstractions and OptimisticJpaSaveSupport"
        wellnessModule -> sharedModule "Uses shared abstractions and OptimisticJpaSaveSupport"
        subscriptionsModule -> sharedModule "Uses shared abstractions and OptimisticJpaSaveSupport"
        moderationModule -> sharedModule "Uses shared abstractions and OptimisticJpaSaveSupport"

        profilesModule -> subscriptionsModule "Checks max allowed trusted contacts via SubscriptionAllowanceFacade"
        commutingModule -> profilesModule "Resolves linked trusted contacts and notification preferences via ProfileDirectoryFacade"
        communityModule -> profilesModule "Enriches author and reporter names via ProfileDirectoryFacade"
        communityModule -> commutingModule "Verifies completed commutes via CommuteVerificationFacade"
        communityModule -> moderationModule "Submits service points and risk zones via ModerationQueueFacade"
        moderationModule -> communityModule "Applies approval/rejection decisions via CommunityTargetGovernanceFacade"

        // Persistence access
        iamModule -> database "users_network_schema (auth_sessions, password_reset_tokens)"
        profilesModule -> database "users_network_schema (user_accounts, trust_links, notification_preferences)"
        commutingModule -> database "commute_incident_schema (commutes, telemetry_pings, safety_incidents, alert_notifications)"
        communityModule -> database "community_data_schema (service_points, service_opening_intervals, service_votes, risk_zones, route_ratings, community_posts)"
        wellnessModule -> database "wellness_schema (rest_sessions, sleep_goals)"
        subscriptionsModule -> database "subscription_schema (9 tables: plans, subscriptions, members, payments, benefits, claims, referrals)"
        moderationModule -> database "moderation_schema (moderation_reports, moderation_actions)"
    }

    views {
        systemContext solution "SystemContext" "System Context diagram for the Noxway Ecosystem." {
            include worker
            include companion
            include admin
            include solution
            include osmSystem
            include emailProvider
            include offlinePaymentNetwork
            autoLayout lr
        }

        container solution "ContainerView" "Container diagram for the Noxway Ecosystem." {
            include worker
            include companion
            include admin
            include singlePageApp
            include noxwayApi
            include database
            include osmSystem
            include emailProvider
            include offlinePaymentNetwork
            autoLayout lr
        }

        component noxwayApi "NoxwayApiComponents" "Component diagram of the Noxway Platform REST API." {
            include iamModule
            include profilesModule
            include commutingModule
            include communityModule
            include wellnessModule
            include subscriptionsModule
            include moderationModule
            include sharedModule
            include singlePageApp
            include database
            include emailProvider
            autoLayout lr
        }

        styles {
            element "Person" {
                shape Person
                background "#08427b"
                color "#ffffff"
            }
            element "Software System" {
                background "#1168bd"
                color "#ffffff"
            }
            element "External System" {
                background "#6c757d"
                color "#ffffff"
            }
            element "Container" {
                background "#438dd5"
                color "#ffffff"
            }
            element "Web Browser" {
                shape WebBrowser
                background "#2563eb"
                color "#ffffff"
            }
            element "Server-side Application" {
                shape Shell
                background "#1e293b"
                color "#ffffff"
            }
            element "Database" {
                shape Cylinder
                background "#0f766e"
                color "#ffffff"
            }
            element "Component" {
                background "#85bbf0"
                color "#000000"
            }
        }

        theme default
    }

    properties {
        structurizr.groupSeparator "/"
    }
}
