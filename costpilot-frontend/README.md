# CostPilot — FinOps Agent UI (merged build)

React/Vite frontend. Drop this into your existing project — `package.json`
and `index.html` are unchanged from your original upload (same dependencies:
recharts, lucide-react), so `npm install && npm run dev` works as-is.

## What changed vs your original build

Kept from your build: recharts charts, lucide-react icons, working mobile
nav, functional topbar selects, modular component structure.

Kept from the HTML mockup: the state loop. Approving a recommendation now
actually removes it from Recommendations/Approvals, creates a real pending
item in Terraform Center, animates through the apply pipeline, and appends
rows to the Audit Log — all from one shared state object in `App()`. Nothing
is cosmetic-only anymore.

Added: a Resource Detail page (`Resources` → click an EC2 row) whose
"Generate Terraform plan" button feeds the same pipeline. Removed: the
duplicate "Savings Opportunities" sidebar entry, and the very small (7–9px)
font sizes — base is 13–15px now throughout.

Scoped out for this pass: Policies, Settings, Automations. They're safe to
add back the same way — a page component plus a nav entry — once the core
loop is presenting well; they don't need to touch shared state.

## Backend integration points

Mock data lives at the top of `src/main.jsx`. Replace with calls to:

- `GET /api/costs` → costSeries
- `GET /api/resources` → resourceList
- `GET /api/recommendations` → initialRecs
- `POST /api/recommendations/{id}/approve` → handleApprove
- `POST /api/recommendations/{id}/reject` → handleReject
- `POST /api/terraform/{id}/apply` → handleApply
- `GET /api/audit-logs` → audit
- `POST /api/agent/runs` → AgentActivity's script
