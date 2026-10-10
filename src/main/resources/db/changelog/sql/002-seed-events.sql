DELETE FROM events
WHERE slug IN (
  'volleyball-day-feb-2026',
  'job-networking-meetup-feb-2026',
  'football-tournament-march-2026',
  'mafia-night-march-2026',
  'summer-picnic-aug-2025',
  'outdoor-volleyball-sept-2025'
);

INSERT INTO events (
  id, slug, title, date, time, location, category, status, description, full_description,
  goal, organizer, location_link, map_embed_url, special_note, attendees, highlights,
  images, agenda, event_highlights, pricing, created_at, updated_at
) VALUES (
  '11111111-1111-4111-8111-111111111111',
  'volleyball-day-feb-2026',
  'PEN Club Volleyball Day',
  DATE '2026-02-21',
  '6:30 PM - 7:30 PM',
  'Camp4 Climbing Hall, Warsaw',
  'PLAY',
  'UPCOMING',
  'A little movement, a little team spirit, and lots of energy! Join us for volleyball - professional level not required.',
  'As PEN Club, we are announcing our next activity - and this time it is all about movement, team spirit, and energy. On February 21st at 6:30 PM, we are gathering at Camp4 Climbing Hall in Warsaw for a Volleyball Day that is about much more than just the sport itself. This meetup is an opportunity to spend time together as a team, meet new people, and strengthen our community even more. Whether you are an experienced player or someone who just wants to show up, enjoy the atmosphere, and cheer for your teammates - you are more than welcome. Professional level is absolutely not required. The main goal is simple: move together and have fun. Please note: The participation fee will be announced once confirmed. Spots are limited, so stay tuned for updates.',
  'Bringing the community together through sport - not just to play volleyball, but to spend quality time as a team, meet new people, and strengthen our bonds.',
  'PEN Club Team',
  'https://www.google.com/maps/place/Camp4+Climbing+Hall/@52.1436493,21.0049696,17z',
  'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2448.487182153182!2d21.0049696!3d52.1436493!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x4719325dd1e12eff%3A0x80451bd8ae247323!2sCamp4%20Climbing%20Hall!5e0!3m2!1sen!2spl!4v1771708901891!5m2!1sen!2spl',
  'Participation Fee: The exact participation fee will be announced once confirmed. Spots are limited - stay tuned to our channels for updates!',
  NULL,
  '["Team spirit and community bonding through sport","Open to all skill levels - beginners and experienced players alike","Great opportunity to meet new people and make new friends","Fun and energetic atmosphere for everyone","Strengthen the PEN Club community together","Move, play, and enjoy - the main goal is to have fun"]',
  '[]',
  NULL,
  NULL,
  NULL,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);

INSERT INTO events (
  id, slug, title, date, time, location, category, status, description, full_description,
  goal, organizer, location_link, map_embed_url, special_note, attendees, highlights,
  images, agenda, event_highlights, pricing, created_at, updated_at
) VALUES (
  '22222222-2222-4222-8222-222222222222',
  'job-networking-meetup-feb-2026',
  'Job Networking & Support Meetup',
  DATE '2026-02-15',
  '12:00 PM - 2:00 PM',
  'Green Caffe Nero, Warsaw, Poland',
  'NETWORK',
  'COMPLETED',
  'PEN Club is starting a new phase with practical job networking and support.',
  'On Sunday, February 15th, 2026, PEN Club hosted a transformative networking meetup that brought together 28 members of the Azerbaijani community in Poland. This event marked a new phase in the club mission, focusing on real support between people actively seeking employment opportunities in Poland and people already established in their careers. Participants shared experiences, exchanged insights, reviewed CVs and LinkedIn profiles, and built professional relationships in a warm collaborative setting.',
  'PEN Club''s goal is simple: To move forward together, not alone.',
  'PEN Club Team',
  'https://maps.app.goo.gl/trXVGZfHtWRW1PmU9',
  'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2444.8582558091102!2d21.0217404!3d52.209623300000004!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x471eccdefa5c150b%3A0x2cd5168208e55a86!2sGreen%20Caff%C3%A8%20Nero!5e0!3m2!1sen!2spl!4v1771243121410!5m2!1sen!2spl',
  NULL,
  28,
  '["Real-life experiences about finding a job in Poland","Communication and networking opportunities","Exchange of ideas regarding CV and LinkedIn improvement","New connections and strong relationships","Opportunity to expand your network and support others"]',
  '[{"url":"/images/events/job-networking-feb-2026.jpeg","alt":"Job Networking & Support Meetup - Group photo of attendees","caption":"Our amazing community coming together for networking and support"}]',
  '[{"time":"12:00 PM - 12:15 PM","title":"Welcome & Introduction","description":"Opening remarks by PEN Club organizers, introduction to the event format, and an ice-breaking session."},{"time":"12:15 PM - 12:45 PM","title":"Panel Discussion: Job Market Insights","description":"Employed professionals shared their experiences about finding jobs in Poland."},{"time":"12:45 PM - 1:15 PM","title":"CV & LinkedIn Review Sessions","description":"Small group breakout sessions for one-on-one CV reviews and LinkedIn profile optimization."},{"time":"1:15 PM - 1:45 PM","title":"Networking & Relationship Building","description":"Open networking time with refreshments and contact exchange."},{"time":"1:45 PM - 2:00 PM","title":"Closing & Next Steps","description":"Participants shared key takeaways and commitments to support each other."}]',
  '["Over 15 CV reviews conducted with personalized feedback","Multiple LinkedIn connections made between job seekers and employers","3 potential job leads identified during networking sessions","Formation of 5 ongoing mentorship pairs","Shared resources including job boards, recruitment agencies, and professional groups","Commitment from attendees to continue supporting each other beyond the event"]',
  NULL,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);

INSERT INTO events (
  id, slug, title, date, time, location, category, status, description, full_description,
  goal, organizer, location_link, map_embed_url, special_note, attendees, highlights,
  images, agenda, event_highlights, pricing, created_at, updated_at
) VALUES (
  '33333333-3333-4333-8333-333333333333',
  'football-tournament-march-2026',
  'Football Tournament',
  DATE '2026-03-15',
  '3:00 PM - 6:00 PM',
  'City Sports Complex',
  'PLAY',
  'UPCOMING',
  'Join us for an exciting football tournament! Form teams and compete for the championship trophy.',
  'Get ready for an action-packed afternoon of football! This tournament brings together the Azerbaijani community in Poland for friendly competition and team building.',
  'Building community through sports and healthy competition.',
  'PEN Club Sports Committee',
  NULL,
  NULL,
  NULL,
  NULL,
  '["Team-based competition format","Championship trophy for winners","Refreshments provided","All skill levels welcome","Great opportunity to meet new people"]',
  '[]',
  NULL,
  NULL,
  NULL,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);

INSERT INTO events (
  id, slug, title, date, time, location, category, status, description, full_description,
  goal, organizer, location_link, map_embed_url, special_note, attendees, highlights,
  images, agenda, event_highlights, pricing, created_at, updated_at
) VALUES (
  '44444444-4444-4444-8444-444444444444',
  'mafia-night-march-2026',
  'Mafia Night',
  DATE '2026-03-22',
  '7:00 PM - 11:00 PM',
  'Community Center',
  'ENJOY',
  'UPCOMING',
  'Strategic Mafia game night with multiple rounds. Test your detective skills and social deduction!',
  'Join us for an evening of mystery, strategy, and fun! Our Mafia night features multiple rounds of the classic social deduction game.',
  'Entertainment and social bonding through strategic gameplay.',
  'PEN Club Games Team',
  NULL,
  NULL,
  NULL,
  NULL,
  '["Multiple game rounds","Experienced moderators","Snacks and drinks provided","Prizes for best players","Perfect for making new friends"]',
  '[]',
  NULL,
  NULL,
  NULL,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);

INSERT INTO events (
  id, slug, title, date, time, location, category, status, description, full_description,
  goal, organizer, location_link, map_embed_url, special_note, attendees, highlights,
  images, agenda, event_highlights, pricing, created_at, updated_at
) VALUES (
  '55555555-5555-4555-8555-555555555555',
  'summer-picnic-aug-2025',
  'Summer Picnic & Games Festival',
  DATE '2025-08-16',
  '12:00 PM - 6:00 PM',
  'Outdoor Park, Warsaw',
  'ENJOY',
  'COMPLETED',
  'Epic outdoor picnic with volleyball, mafia, intellectual games, board games, music, pizza, and samovar tea!',
  'On August 16th, 2025, PEN Club organized one of the most memorable events of the summer - a grand picnic and games festival that brought together 65 members of the community. The event offered two participation packages: the Full Package included pizza and all activities, while the Basic Package allowed participants to bring their own food and enjoy the games and entertainment. Multiple activity stations included volleyball, board games, intellectual competitions, Mafia sessions, music, drinks, and traditional Azerbaijani samovar tea.',
  'Creating an unforgettable summer experience that combines sports, intellectual activities, cultural traditions, and community bonding in a beautiful outdoor setting.',
  'PEN Club Events Team',
  'https://maps.app.goo.gl/Gcipa9CUG4X9tMN98',
  'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2005.315747698821!2d20.999647943340328!3d52.214345269053595!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x471ecd005716fdf3%3A0xf12423803d05bdd7!2sLe%C5%BCaki%20kamienne%20na%20Polu%20Mokotowskim!5e0!3m2!1sen!2spl!4v1771243596555!5m2!1sen!2spl',
  'This event featured two package options: Full Package (40 AZN) with pizza included, and Basic Package (20 AZN) where participants brought their own food. Both packages included access to all activities, games, music, drinks, and the traditional samovar tea.',
  65,
  '["Volleyball matches for sports enthusiasts","Strategic Mafia games testing social deduction skills","Intellectual competitions with prizes","Board games for relaxed entertainment","Live music and dancing","Delicious pizza and snacks","Cold refreshing drinks","Traditional samovar tea experience","Two package options for flexibility","All-day entertainment from noon to evening"]',
  '[]',
  NULL,
  '["65 participants enjoying a full day of activities","Perfect summer weather for outdoor celebration","Multiple volleyball matches with rotating teams","Intense Mafia game sessions with experienced moderators","Intellectual quiz competitions with prizes for winners","Variety of board games for all ages and interests","Live music creating festive atmosphere","Traditional Azerbaijani samovar tea service","Pizza feast and cold refreshing drinks","Successful blend of sports, games, and cultural elements"]',
  '{"full":"40 AZN (includes pizza)","basic":"20 AZN (bring your own food)"}',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);

INSERT INTO events (
  id, slug, title, date, time, location, category, status, description, full_description,
  goal, organizer, location_link, map_embed_url, special_note, attendees, highlights,
  images, agenda, event_highlights, pricing, created_at, updated_at
) VALUES (
  '66666666-6666-4666-8666-666666666666',
  'outdoor-volleyball-sept-2025',
  'Outdoor Volleyball - Last Summer Games',
  DATE '2025-09-12',
  '5:00 PM - 7:00 PM',
  'Volleyball Court, Warsaw',
  'PLAY',
  'COMPLETED',
  'Final outdoor volleyball session before winter!',
  'On September 12th, 2025, PEN Club organized the last outdoor volleyball session of the summer season. With 32 enthusiastic participants, the event marked the transition from free outdoor games to indoor facilities for colder months. Players of all skill levels formed balanced teams, played friendly competitive matches, and enjoyed a warm community atmosphere as the season closed.',
  'Making the most of summer weather while building community through sports before transitioning to indoor facilities.',
  'PEN Club Sports Committee',
  'https://maps.app.goo.gl/Rj57CGbqSG3mzAL89',
  'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2444.5506430935175!2d20.9962002!3d52.215213!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x471eccc08052518d%3A0x88e686ef3b99e439!2sVolleyball%20Court!5e0!3m2!1sen!2spl!4v1771243149775!5m2!1sen!2spl',
  'Important: Summer is almost over! Once the weather gets colder, games will be organized indoors and paid. Do not miss this opportunity for free outdoor volleyball!',
  32,
  '["Last chance to play outdoors before winter","Free participation with no venue costs","All skill levels welcome","Great weather and atmosphere","Community building through sports","Information about upcoming indoor sessions"]',
  '[]',
  NULL,
  '["32 active participants enjoying the last outdoor session","Perfect weather conditions for volleyball","6 competitive matches played throughout the event","Mix of experienced players and beginners playing together","Community bonding before transitioning to indoor games","Important announcement about winter indoor sessions","Group photo capturing the memorable moment"]',
  NULL,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);
