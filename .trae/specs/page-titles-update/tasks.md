# Tasks

## Phase 1: Configuration Updates

- [x] Task 1: Update index.html base title
  - Modify `petshop-vue/index.html` to change "Vite App" to "宠物服务平台"
  - This provides a fallback title

## Phase 2: Router Updates

- [x] Task 2: Add navigation guard and title meta to router
  - Modify `petshop-vue/src/router/index.ts`
  - Add `title` property to each route's `meta` object
  - Create `beforeEach` navigation guard to update `document.title`
  - Handle dynamic route params for detail pages (e.g., `:id`)

## Phase 3: Title Assignments

- [x] Task 3: Add titles to user-side routes (28 routes)
  - All routes under `/user` path

- [x] Task 4: Add titles to merchant-side routes (16 routes)
  - All routes under `/merchant` path

- [x] Task 5: Add titles to admin-side routes (20 routes)
  - All routes under `/admin` path

- [x] Task 6: Add titles to auth routes (4 routes)
  - `/login`, `/register`, `/merchant/login`, `/merchant/register`

## Task Dependencies
- Task 2 depends on Task 1
- Tasks 3, 4, 5, 6 depend on Task 2
