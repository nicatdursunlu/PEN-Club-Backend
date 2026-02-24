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

## Admin User
- [x] 15. Add existsByRole(Role) to UserRepository
- [x] 16. Create AdminUserInitializer — seed admin on startup from env vars (idempotent)
- [x] 17. Update application.yml — add admin credential env var placeholders
- [x] 18. Add registerAdmin() to AuthService (ADMIN role)
- [x] 19. Add POST /auth/admin/register endpoint (requires ADMIN role)
- [x] 20. Update SecurityConfig — @EnableMethodSecurity already active, /auth/admin/register not in PUBLIC_ENDPOINTS

## Dashboard API (ADMIN only)
- [x] 21. Add countByRole(Role) to UserRepository
- [x] 22. Create DashboardResponseDto
- [x] 23. Create DashboardService
- [x] 24. Create DashboardController — GET /dashboard (ADMIN only)
- [x] 25. Update SecurityConfig — protect /dashboard/** with ADMIN role

## Upcoming & Past Events Endpoints
- [x] 15. Add getUpcomingEvents() and getPastEvents() to EventService with date parsing + EventStatus fallback
- [x] 16. Add GET /events/upcoming and GET /events/past to EventController
