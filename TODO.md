# Events Feature Implementation

## Plan
- [x] 1. Create Event enums (EventCategory, EventStatus)
- [x] 2. Create Event entity
- [x] 3. Create EventRepository
- [x] 4. Create EventService (returns raw Entity — to be updated)
- [x] 5. Create EventController (returns raw Entity — to be updated)
- [x] 6. Create EventResponseDto (clean DTO for Controller/Service layers)
- [x] 7. Update EventService to return EventResponseDto
- [x] 8. Update EventController to use EventResponseDto
- [x] 9. Update SecurityConfig — GET /events/** public, write endpoints protected
- [x] 10. Create EventDataInitializer — seed 6 events on startup (idempotent)
- [ ] 11. Test the CRUD endpoints

## Get All Events - Summary DTO
- [x] 12. Create EventSummaryDto (id=slug, title, date, time, location, description, category)
- [x] 13. Update EventService.getAllEvents() to return List<EventSummaryDto>
- [x] 14. Update EventController.getAllEvents() to return List<EventSummaryDto>

## Upcoming & Past Events Endpoints
- [x] 15. Add getUpcomingEvents() and getPastEvents() to EventService with date parsing + EventStatus fallback
- [x] 16. Add GET /events/upcoming and GET /events/past to EventController
