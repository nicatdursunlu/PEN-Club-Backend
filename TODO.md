# Event Date & Constraint Fix

## Tasks

- [x] Plan confirmed by user
- [x] Fix `Event.java` — change `date` to `LocalDate`, add `columnDefinition` to `category` and `status`
- [x] Fix `EventRequestDto.java` — change `date` to `LocalDate`
- [x] Fix `EventResponseDto.java` — change `date` to `LocalDate`
- [x] Fix `EventSummaryDto.java` — change `date` to `LocalDate`
- [x] Fix `EventService.java` — simplify filtering using `LocalDate.now()`
- [x] Fix `EventDataInitializer.java` — change all date strings to `LocalDate.of(...)`
