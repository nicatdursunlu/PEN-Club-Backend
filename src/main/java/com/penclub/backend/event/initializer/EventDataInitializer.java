package com.penclub.backend.event.initializer;

import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventCategory;
import com.penclub.backend.event.entity.EventStatus;
import com.penclub.backend.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDataInitializer implements CommandLineRunner {

    private final EventRepository eventRepository;

    @Override
    public void run(String... args) {
        seedEvents();
    }

    private void seedEvents() {
        log.info("Starting event data seeding...");

        seedVolleyballDayFeb2026();
        seedJobNetworkingMeetupFeb2026();
        seedFootballTournamentMarch2026();
        seedMafiaNightMarch2026();
        seedSummerPicnicAug2025();
        seedOutdoorVolleyballSept2025();

        log.info("Event data seeding completed.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Volleyball Day – February 2026
    // ─────────────────────────────────────────────────────────────────────────
    private void seedVolleyballDayFeb2026() {
        String slug = "volleyball-day-feb-2026";
        if (eventRepository.existsBySlug(slug)) {
            log.debug("Event '{}' already exists — skipping.", slug);
            return;
        }

        Event event = Event.builder()
                .slug(slug)
                .title("🏐 PEN Club Volleyball Day 🏐")
                .date("February 21, 2026")
                .time("6:30 PM - 7:30 PM")
                .location("Camp4 Climbing Hall, Warsaw")
                .category(EventCategory.SPORTS)
                .status(EventStatus.UPCOMING)
                .description("A little movement, a little team spirit, and lots of energy! Join us for volleyball — professional level not required.")
                .fullDescription("""
                        As PEN Club, we're announcing our next activity — and this time it's all about movement, team spirit, and energy! 😍

                        On February 21st at 6:30 PM, we're gathering at Camp4 Climbing Hall in Warsaw for a Volleyball Day that's about much more than just the sport itself.

                        This meetup is an opportunity to spend time together as a team, meet new people, and strengthen our community even more. Whether you're an experienced player or someone who just wants to show up, enjoy the atmosphere, and cheer for your teammates — you are more than welcome! 🏐

                        Professional level is absolutely not required. The main goal is simple: move together and have fun. ✌️

                        ⚠️ Please note: The participation fee will be announced once confirmed. Spots are limited, so stay tuned for updates!""")
                .highlights(List.of(
                        "Team spirit and community bonding through sport 🏐",
                        "Open to all skill levels — beginners and experienced players alike",
                        "Great opportunity to meet new people and make new friends",
                        "Fun and energetic atmosphere for everyone",
                        "Strengthen the PEN Club community together",
                        "Move, play, and enjoy — the main goal is to have fun! ✌️"
                ))
                .goal("Bringing the community together through sport — not just to play volleyball, but to spend quality time as a team, meet new people, and strengthen our bonds.")
                .images(List.of())
                .organizer("PEN Club Team")
                .locationLink("https://www.google.com/maps/place/Camp4+Climbing+Hall/@52.1436493,21.0049696,17z")
                .mapEmbedUrl("https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2448.487182153182!2d21.0049696!3d52.1436493!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x4719325dd1e12eff%3A0x80451bd8ae247323!2sCamp4%20Climbing%20Hall!5e0!3m2!1sen!2spl!4v1771708901891!5m2!1sen!2spl")
                .specialNote("⚠️ Participation Fee: The exact participation fee will be announced once confirmed. Spots are limited — stay tuned to our channels for updates! 👥")
                .build();

        eventRepository.save(event);
        log.info("Seeded event: '{}'", slug);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Job Networking & Support Meetup – February 2026
    // ─────────────────────────────────────────────────────────────────────────
    private void seedJobNetworkingMeetupFeb2026() {
        String slug = "job-networking-meetup-feb-2026";
        if (eventRepository.existsBySlug(slug)) {
            log.debug("Event '{}' already exists — skipping.", slug);
            return;
        }

        Event event = Event.builder()
                .slug(slug)
                .title("Job Networking & Support Meetup")
                .date("February 15, 2026")
                .time("12:00 PM - 2:00 PM")
                .location("Green Caffè Nero, Warsaw, Poland")
                .category(EventCategory.NETWORKING)
                .status(EventStatus.COMPLETED)
                .attendees(28)
                .description("🚀 PEN Club is starting a new phase…")
                .fullDescription("""
                        On Sunday, February 15th, 2026, PEN Club hosted a transformative networking meetup that brought together 28 members of the Azerbaijani community in Poland. This event marked a new phase in our club's mission - focusing on real, tangible support between those actively seeking employment opportunities in Poland and those who are currently established in their careers.

                        The atmosphere was warm and collaborative as participants gathered to share experiences, exchange insights, and build meaningful professional relationships. The event created a safe space where job seekers could openly discuss their challenges while employed professionals offered guidance, mentorship, and practical advice based on their own journeys in the Polish job market.

                        Throughout the two-hour session, attendees engaged in deep conversations about navigating the Polish employment landscape, understanding cultural nuances in the workplace, and leveraging professional networks effectively. The energy in the room was palpable as connections were made, LinkedIn profiles were reviewed on the spot, and CVs were critiqued with constructive feedback.""")
                .agenda(List.of(
                        new Event.AgendaItem(
                                "12:00 PM - 12:15 PM",
                                "Welcome & Introduction",
                                "Opening remarks by PEN Club organizers, introduction to the event format, and ice-breaking session where each participant shared their current professional status and what they hoped to gain from the meetup."
                        ),
                        new Event.AgendaItem(
                                "12:15 PM - 12:45 PM",
                                "Panel Discussion: Job Market Insights",
                                "Employed professionals shared their experiences about finding jobs in Poland, discussing topics like application strategies, interview preparation, and workplace culture. Q&A session allowed job seekers to ask specific questions about different industries and career paths."
                        ),
                        new Event.AgendaItem(
                                "12:45 PM - 1:15 PM",
                                "CV & LinkedIn Review Sessions",
                                "Small group breakout sessions where participants paired up for one-on-one CV reviews and LinkedIn profile optimization. Experienced professionals provided personalized feedback on resumes, cover letters, and online professional presence."
                        ),
                        new Event.AgendaItem(
                                "1:15 PM - 1:45 PM",
                                "Networking & Relationship Building",
                                "Open networking time with refreshments. Participants exchanged contact information, discussed potential job opportunities, and formed mentorship connections. Several attendees discovered mutual professional interests and planned follow-up meetings."
                        ),
                        new Event.AgendaItem(
                                "1:45 PM - 2:00 PM",
                                "Closing & Next Steps",
                                "Wrap-up session where participants shared key takeaways, success stories from the day, and commitments to support each other going forward. Announcement of future PEN Club networking events and initiatives."
                        )
                ))
                .eventHighlights(List.of(
                        "Over 15 CV reviews conducted with personalized feedback",
                        "Multiple LinkedIn connections made between job seekers and employers",
                        "3 potential job leads identified during networking sessions",
                        "Formation of 5 ongoing mentorship pairs",
                        "Shared resources including job boards, recruitment agencies, and professional groups",
                        "Commitment from attendees to continue supporting each other beyond the event"
                ))
                .highlights(List.of(
                        "Real-life experiences about finding a job in Poland",
                        "Communication and networking opportunities",
                        "Exchange of ideas regarding CV / LinkedIn improvement",
                        "New connections and strong relationships",
                        "Opportunity to expand your network and support others"
                ))
                .goal("PEN Club's goal is simple: To move forward together, not alone.")
                .images(List.of(
                        new Event.EventImage(
                                "/images/events/job-networking-feb-2026.jpeg",
                                "Job Networking & Support Meetup - Group photo of attendees",
                                "Our amazing community coming together for networking and support"
                        )
                ))
                .organizer("PEN Club Team")
                .locationLink("https://maps.app.goo.gl/trXVGZfHtWRW1PmU9")
                .mapEmbedUrl("https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2444.8582558091102!2d21.0217404!3d52.209623300000004!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x471eccdefa5c150b%3A0x2cd5168208e55a86!2sGreen%20Caff%C3%A8%20Nero!5e0!3m2!1sen!2spl!4v1771243121410!5m2!1sen!2spl")
                .build();

        eventRepository.save(event);
        log.info("Seeded event: '{}'", slug);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Football Tournament – March 2026
    // ─────────────────────────────────────────────────────────────────────────
    private void seedFootballTournamentMarch2026() {
        String slug = "football-tournament-march-2026";
        if (eventRepository.existsBySlug(slug)) {
            log.debug("Event '{}' already exists — skipping.", slug);
            return;
        }

        Event event = Event.builder()
                .slug(slug)
                .title("Football Tournament")
                .date("March 15, 2026")
                .time("3:00 PM - 6:00 PM")
                .location("City Sports Complex")
                .category(EventCategory.SPORTS)
                .status(EventStatus.UPCOMING)
                .description("Join us for an exciting football tournament! Form teams and compete for the championship trophy.")
                .fullDescription("Get ready for an action-packed afternoon of football! This tournament brings together the Azerbaijani community in Poland for friendly competition and team building.")
                .highlights(List.of(
                        "Team-based competition format",
                        "Championship trophy for winners",
                        "Refreshments provided",
                        "All skill levels welcome",
                        "Great opportunity to meet new people"
                ))
                .goal("Building community through sports and healthy competition.")
                .images(List.of())
                .organizer("PEN Club Sports Committee")
                .build();

        eventRepository.save(event);
        log.info("Seeded event: '{}'", slug);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. Mafia Night – March 2026
    // ─────────────────────────────────────────────────────────────────────────
    private void seedMafiaNightMarch2026() {
        String slug = "mafia-night-march-2026";
        if (eventRepository.existsBySlug(slug)) {
            log.debug("Event '{}' already exists — skipping.", slug);
            return;
        }

        Event event = Event.builder()
                .slug(slug)
                .title("Mafia Night")
                .date("March 22, 2026")
                .time("7:00 PM - 11:00 PM")
                .location("Community Center")
                .category(EventCategory.GAMES)
                .status(EventStatus.UPCOMING)
                .description("Strategic Mafia game night with multiple rounds. Test your detective skills and social deduction!")
                .fullDescription("Join us for an evening of mystery, strategy, and fun! Our Mafia night features multiple rounds of the classic social deduction game.")
                .highlights(List.of(
                        "Multiple game rounds",
                        "Experienced moderators",
                        "Snacks and drinks provided",
                        "Prizes for best players",
                        "Perfect for making new friends"
                ))
                .goal("Entertainment and social bonding through strategic gameplay.")
                .images(List.of())
                .organizer("PEN Club Games Team")
                .build();

        eventRepository.save(event);
        log.info("Seeded event: '{}'", slug);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. Summer Picnic & Games Festival – August 2025
    // ─────────────────────────────────────────────────────────────────────────
    private void seedSummerPicnicAug2025() {
        String slug = "summer-picnic-aug-2025";
        if (eventRepository.existsBySlug(slug)) {
            log.debug("Event '{}' already exists — skipping.", slug);
            return;
        }

        Event event = Event.builder()
                .slug(slug)
                .title("Summer Picnic & Games Festival")
                .date("August 16, 2025")
                .time("12:00 PM - 6:00 PM")
                .location("Outdoor Park, Warsaw")
                .category(EventCategory.SOCIAL)
                .status(EventStatus.COMPLETED)
                .attendees(65)
                .description("🌿 Epic outdoor picnic with volleyball, mafia, intellectual games, board games, music, pizza, and samovar tea!")
                .fullDescription("""
                        On August 16th, 2025, PEN Club organized one of the most memorable events of the summer - a grand picnic and games festival that brought together 65 members of our vibrant community. This wasn't just another outdoor gathering; it was a celebration of friendship, culture, and the joy of spending quality time together in nature.

                        The event offered two participation packages to accommodate everyone's preferences. The Full Package (40 AZN) included delicious pizza along with all activities, while the Basic Package (20 AZN) allowed participants to bring their own food and enjoy all the games and entertainment. This flexible approach ensured that everyone could join regardless of their budget or dietary preferences.

                        From noon until evening, the park was filled with laughter, competitive spirit, and the warmth of community bonding. Multiple activity stations were set up across the venue - volleyball nets for sports enthusiasts, cozy spots for board games, areas for intellectual competitions with exciting prizes, and of course, the legendary Mafia game sessions that kept everyone on their toes. The highlight for many was the traditional Azerbaijani samovar tea service, which added a special cultural touch to the gathering and reminded everyone of home. Music played throughout the day, creating the perfect atmosphere for both energetic activities and relaxed conversations. The combination of sports, strategy games, intellectual challenges, delicious food, and refreshing drinks made this event truly unforgettable.""")
                .eventHighlights(List.of(
                        "65 participants enjoying a full day of activities",
                        "Perfect summer weather for outdoor celebration",
                        "Multiple volleyball matches with rotating teams",
                        "Intense Mafia game sessions with experienced moderators",
                        "Intellectual quiz competitions with prizes for winners",
                        "Variety of board games for all ages and interests",
                        "Live music creating festive atmosphere",
                        "Traditional Azerbaijani samovar tea service",
                        "Pizza feast and cold refreshing drinks",
                        "Successful blend of sports, games, and cultural elements"
                ))
                .highlights(List.of(
                        "Volleyball matches for sports enthusiasts 🏐",
                        "Strategic Mafia games testing social deduction skills 🕵️‍♂️",
                        "Intellectual competitions with exciting prizes 🧠",
                        "Board games for relaxed entertainment 🎲",
                        "Live music and dancing 🎵",
                        "Delicious pizza and snacks 🍕",
                        "Cold refreshing drinks 🍹",
                        "Traditional samovar tea experience ☕",
                        "Two package options (Full & Basic) for flexibility",
                        "All-day entertainment from noon to evening"
                ))
                .goal("Creating an unforgettable summer experience that combines sports, intellectual activities, cultural traditions, and community bonding in a beautiful outdoor setting.")
                .images(List.of())
                .organizer("PEN Club Events Team")
                .locationLink("https://maps.app.goo.gl/Gcipa9CUG4X9tMN98")
                .mapEmbedUrl("https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2005.315747698821!2d20.999647943340328!3d52.214345269053595!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x471ecd005716fdf3%3A0xf12423803d05bdd7!2sLe%C5%BCaki%20kamienne%20na%20Polu%20Mokotowskim!5e0!3m2!1sen!2spl!4v1771243596555!5m2!1sen!2spl")
                .pricing(new Event.Pricing("40 AZN (includes pizza)", "20 AZN (bring your own food)"))
                .specialNote("🌟 This event featured two package options: Full Package (40 AZN) with pizza included, and Basic Package (20 AZN) where participants brought their own food. Both packages included access to all activities, games, music, drinks, and the traditional samovar tea!")
                .build();

        eventRepository.save(event);
        log.info("Seeded event: '{}'", slug);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. Outdoor Volleyball – September 2025
    // ─────────────────────────────────────────────────────────────────────────
    private void seedOutdoorVolleyballSept2025() {
        String slug = "outdoor-volleyball-sept-2025";
        if (eventRepository.existsBySlug(slug)) {
            log.debug("Event '{}' already exists — skipping.", slug);
            return;
        }

        Event event = Event.builder()
                .slug(slug)
                .title("Outdoor Volleyball - Last Summer Games")
                .date("September 12, 2025")
                .time("5:00 PM - 7:00 PM")
                .location("Volleyball Court, Warsaw")
                .category(EventCategory.SPORTS)
                .status(EventStatus.COMPLETED)
                .attendees(32)
                .description("🏐 Final outdoor volleyball session before winter!")
                .fullDescription("""
                        On September 12th, 2025, PEN Club organized what would be the last outdoor volleyball session of the summer season. With 32 enthusiastic participants, this event marked a bittersweet moment as we prepared to transition from free outdoor games to indoor facilities for the colder months ahead.

                        The weather was perfect for volleyball - warm but not too hot, with a gentle breeze that made the game even more enjoyable. Players of all skill levels came together, forming balanced teams and engaging in friendly but competitive matches. The atmosphere was filled with laughter, cheers, and the satisfying sound of the volleyball being spiked and served across the net.

                        As the sun began to set, there was a collective appreciation for these outdoor sessions that had brought the community together throughout the summer. The event served as a reminder to make the most of the remaining warm days, as future games would require indoor venue bookings and associated costs.""")
                .eventHighlights(List.of(
                        "32 active participants enjoying the last outdoor session",
                        "Perfect weather conditions for volleyball",
                        "6 competitive matches played throughout the event",
                        "Mix of experienced players and beginners playing together",
                        "Community bonding before transitioning to indoor games",
                        "Important announcement about winter indoor sessions",
                        "Group photo capturing the memorable moment"
                ))
                .highlights(List.of(
                        "Last chance to play outdoors before winter",
                        "Free participation (no venue costs)",
                        "All skill levels welcome",
                        "Great weather and atmosphere",
                        "Community building through sports",
                        "Information about upcoming indoor sessions"
                ))
                .goal("Making the most of summer weather while building community through sports before transitioning to indoor facilities.")
                .images(List.of())
                .organizer("PEN Club Sports Committee")
                .locationLink("https://maps.app.goo.gl/Rj57CGbqSG3mzAL89")
                .mapEmbedUrl("https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2444.5506430935175!2d20.9962002!3d52.215213!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x471eccc08052518d%3A0x88e686ef3b99e439!2sVolleyball%20Court!5e0!3m2!1sen!2spl!4v1771243149775!5m2!1sen!2spl")
                .specialNote("⚠️ Important: Summer is almost over! Once the weather gets colder, games will be organized indoors (paid). Don't miss this opportunity for free outdoor volleyball!")
                .build();

        eventRepository.save(event);
        log.info("Seeded event: '{}'", slug);
    }
}
