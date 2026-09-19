import React, { useEffect, useRef, useState } from "react";
import { createRoot } from "react-dom/client";
import {
  Activity, AlertTriangle, ArrowDownRight, ArrowUpRight, Bot, Bell,
  BrainCircuit, Check, ChevronDown, ChevronRight, ChevronLeft, CircleDollarSign,
  Database, FileCode2, HardDrive, LayoutDashboard, ListChecks, Menu,
  RefreshCw, Search, Server, ShieldCheck, Sparkles, Terminal, UserRound,
  X, Zap, TrendingDown, TrendingUp
} from "lucide-react";
import {
  AreaChart, Area, LineChart, Line, CartesianGrid,
  ResponsiveContainer, Tooltip, XAxis, YAxis
} from "recharts";
import "./styles.css";

/* =====================================================================
   MOCK DATA
   Replace these with your Spring Boot API calls — see README for the
   suggested endpoint contract. Nothing else in this file needs to
   change shape-wise as long as the objects keep these fields.
===================================================================== */

const costSeries = [
  { month: "Apr", actual: 10200, optimized: 10200, forecast: 10200 },
  { month: "May", actual: 11400, optimized: 11400, forecast: 11800 },
  { month: "Jun", actual: 13100, optimized: 13000, forecast: 14200 },
  { month: "Jul", actual: 14800, optimized: 14200, forecast: 16900 },
  { month: "Aug", actual: 16200, optimized: 15100, forecast: 20100 },
  { month: "Sep", actual: 18430, optimized: 15700, forecast: 23840 },
];

const cpuSeries14d = [5, 4, 6, 3, 4, 5, 4, 3, 4, 5, 4, 3, 4, 4].map((v, i) => ({ day: `${i + 1}`, cpu: v }));

const resourceList = [
  { id: "i-0a83f2c1", name: "dev-api-server", type: "EC2", region: "ap-south-1", env: "Development", cpu: 4.2, cost: 2130, status: "Idle", instance: "t3.large", terraform: true },
  { id: "i-29381e", name: "prod-api-server", type: "EC2", region: "ap-south-1", env: "Production", cpu: 42, cost: 4230, status: "Healthy", instance: "m6i.large", terraform: true },
  { id: "i-71293a", name: "staging-worker", type: "EC2", region: "ap-south-1", env: "Staging", cpu: 9.8, cost: 1740, status: "Underutilized", instance: "t3.medium", terraform: true },
  { id: "db-prod-01", name: "orders-database", type: "RDS", region: "ap-south-1", env: "Production", cpu: 19, cost: 7200, status: "Underutilized", instance: "db.t3.large", terraform: true },
  { id: "vol-08321a", name: "orphaned-data-volume", type: "EBS", region: "ap-south-1", env: "Development", cpu: null, cost: 620, status: "Unattached", instance: "gp3 100GB", terraform: true },
  { id: "fn-billing", name: "billing-worker", type: "Lambda", region: "ap-south-1", env: "Production", cpu: null, cost: 490, status: "Healthy", instance: "128MB", terraform: false },
];

const initialRecs = [
  {
    id: "rec1", resource: "i-0a83f2c1", name: "dev-api-server", type: "EC2",
    risk: "low", savings: 1240, from: "t3.large", to: "t3.medium",
    cpu: 4.2, network: 0.03, runtime: "14 days",
    currentCost: 2130, newCost: 890, confidence: 94,
    env: "Development", terraform: true, prodDep: false,
    reasoning: "This development instance has remained below 5% CPU utilization for the last 14 days, with negligible network activity. It is tagged non-production with nothing depending on it, making a downsize safe.",
  },
  {
    id: "rec2", resource: "vol-08321a", name: "orphaned-data-volume", type: "EBS",
    risk: "low", savings: 620, from: "100GB gp3 (attached)", to: "Delete volume",
    cpu: null, network: null, runtime: "21 days unattached",
    currentCost: 620, newCost: 0, confidence: 98,
    env: "Development", terraform: true, prodDep: false,
    reasoning: "This volume has been unattached for 21 days with no snapshots depending on it and no writes since detachment — a strong candidate for deletion.",
  },
  {
    id: "rec3", resource: "db-prod-01", name: "orders-database", type: "RDS",
    risk: "medium", savings: 1460, from: "db.t3.large", to: "db.t3.medium",
    cpu: 19, network: 1.2, runtime: "30 days",
    currentCost: 7200, newCost: 5740, confidence: 71,
    env: "Production", terraform: true, prodDep: true,
    reasoning: "Average CPU has stayed under 20% over 30 days, but this is a production database with active connections. Confidence is lower than the EC2 case — review peak-hour traffic before applying.",
  },
];

const auditSeed = [
  { time: "09:14", actor: "FinOps Agent", action: "Recommend delete", resource: "vol-04421", result: "success" },
  { time: "09:16", actor: "Aarish", action: "Approved", resource: "vol-04421", result: "success" },
  { time: "09:17", actor: "Terraform", action: "Apply", resource: "vol-04421", result: "success" },
];

const activityScript = [
  { t: "info", text: "Agent started scheduled analysis" },
  { t: "ok", text: "Retrieved 47 AWS resources" },
  { t: "ok", text: "Retrieved CloudWatch metrics" },
  { t: "ok", text: "Retrieved Cost Explorer data" },
  { t: "ok", text: "Retrieved Terraform state" },
  { t: "warn", text: "Found 3 optimization opportunities" },
  { t: "info", text: "Analyzing i-0a83f2c1 (dev-api-server)" },
  { t: "info", text: "CPU utilization: 4.2% over 14 days" },
  { t: "ok", text: "Policy check passed — no production dependency" },
  { t: "wait", text: "Queued for human approval" },
];

const money = (n) => `₹${n.toLocaleString("en-IN")}`;

/* =====================================================================
   SMALL SHARED COMPONENTS
===================================================================== */

function Badge({ risk }) {
  return <span className={`badge ${risk}`}><i /> {risk} risk</span>;
}

function PageHeader({ eyebrow, title, subtitle, action }) {
  return (
    <div className="page-header">
      <div><div className="eyebrow">{eyebrow}</div><h1>{title}</h1><p>{subtitle}</p></div>
      {action}
    </div>
  );
}

function Panel({ title, action, children, className = "" }) {
  return (
    <section className={`panel card ${className}`}>
      <div className="panel-head"><h3>{title}</h3>{action}</div>
      {children}
    </section>
  );
}

function KPI({ icon: Icon, label, value, trend, note, tone }) {
  return (
    <div className="kpi card">
      <div className="kpi-top">
        <div className="kpi-icon"><Icon size={17} /></div>
        {trend && <span className={`trend ${tone || "neutral"}`}>
          {tone === "up" ? <ArrowUpRight size={13} /> : tone === "down" ? <ArrowDownRight size={13} /> : null}{trend}
        </span>}
      </div>
      <div className="kpi-value">{value}</div>
      <div className="kpi-label">{label}</div>
      {note && <small>{note}</small>}
    </div>
  );
}

function Toast({ message }) {
  if (!message) return null;
  return <div className="toast"><Check size={16} /> {message}</div>;
}

/* =====================================================================
   RECOMMENDATION DRAWER (slide-over, used from Dashboard / Recommendations / Approvals)
===================================================================== */

function RecommendationDrawer({ rec, close, onApprove, onReject }) {
  if (!rec) return null;
  return (
    <div className="drawer-backdrop" onClick={close}>
      <aside className="drawer" onClick={(e) => e.stopPropagation()}>
        <div className="drawer-head">
          <div><Badge risk={rec.risk} /><h2>Why this recommendation?</h2></div>
          <button className="icon-btn" onClick={close}><X size={18} /></button>
        </div>

        <div className="drawer-resource">
          <Server size={17} />
          <div><b>{rec.name}</b><span>{rec.resource} · {rec.from} → {rec.to}</span></div>
        </div>

        <h3 className="drawer-subhead">Evidence</h3>
        {rec.cpu !== null && (
          <div className="evidence">
            <div><span>CPU utilization</span><b>{rec.cpu}%</b></div>
            <div className="bar"><span style={{ width: `${Math.min(rec.cpu * 2, 100)}%` }} /></div>
          </div>
        )}
        {rec.network !== null && (
          <div className="evidence">
            <div><span>Network</span><b>{rec.network} MB/s</b></div>
            <div className="bar"><span style={{ width: `${Math.min(rec.network * 30, 100)}%` }} /></div>
          </div>
        )}
        <div className="evidence">
          <div><span>Runtime / status</span><b>{rec.runtime}</b></div>
        </div>

        <div className="drawer-cost">
          <div><span>Current</span><b>{money(rec.currentCost)}/mo</b></div>
          <div><span>After optimization</span><b>{money(rec.newCost)}/mo</b></div>
          <div className="save"><span>Potential savings</span><b>{money(rec.savings)}/mo</b></div>
        </div>

        <h3 className="drawer-subhead">Agent reasoning</h3>
        <p className="drawer-copy">"{rec.reasoning}"</p>

        <div className="drawer-facts">
          <div><small>Confidence</small><b>{rec.confidence}%</b></div>
          <div><small>Risk</small><b>{rec.risk.toUpperCase()}</b></div>
          <div><small>Terraform managed</small><b>{rec.terraform ? "Yes" : "No"}</b></div>
        </div>

        <div className="drawer-actions">
          <button className="secondary-btn" onClick={() => onReject(rec)}>Dismiss</button>
          <button className="primary-btn" onClick={() => onApprove(rec)}><Check size={16} /> Send to Terraform</button>
        </div>
      </aside>
    </div>
  );
}

/* =====================================================================
   PAGES
===================================================================== */

function Dashboard({ recs, tfItems, audit, navigate, openRec }) {
  const idle = 3, underutilized = 8, total = 47;
  return (
    <>
      <PageHeader
        eyebrow="OVERVIEW" title="Cloud cost intelligence"
        subtitle="Your AWS estate, optimization opportunities, and agent actions in one place."
        action={<button className="primary-btn" onClick={() => navigate("Agent Activity")}><BrainCircuit size={16} /> Run analysis</button>}
      />
      <div className="kpi-grid">
        <KPI icon={CircleDollarSign} label="Monthly AWS cost" value="₹18,430" trend="+8.2%" note="vs last month" tone="up" />
        <KPI icon={TrendingDown} label="Potential savings" value={money(recs.reduce((a, r) => a + r.savings, 0))} trend="23.2%" note="of current spend" />
        <KPI icon={Zap} label="Realized savings" value="₹2,140" trend="+₹820" note="this month" tone="down" />
        <KPI icon={Server} label="Resources" value={total} note={`${idle} idle · ${underutilized} underutilized`} />
      </div>

      <div className="grid-2 main-chart-row">
        <Panel title="Cost & savings trend">
          <div className="chart-legend">
            <span><i className="dot actual" />Actual</span>
            <span><i className="dot optimized" />With optimizations</span>
          </div>
          <div className="chart">
            <ResponsiveContainer width="100%" height={230}>
              <AreaChart data={costSeries}>
                <defs>
                  <linearGradient id="costFill" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stopColor="#3fc1f0" stopOpacity=".25" />
                    <stop offset="100%" stopColor="#3fc1f0" stopOpacity="0" />
                  </linearGradient>
                </defs>
                <CartesianGrid stroke="#1a222b" vertical={false} />
                <XAxis dataKey="month" stroke="#5b6878" tickLine={false} axisLine={false} fontSize={12} />
                <YAxis stroke="#5b6878" tickLine={false} axisLine={false} fontSize={12} tickFormatter={(v) => `₹${v / 1000}k`} />
                <Tooltip contentStyle={{ background: "#161d26", border: "1px solid #212b35", borderRadius: 10, fontSize: 12 }} />
                <Area type="monotone" dataKey="actual" stroke="#3fc1f0" fill="url(#costFill)" strokeWidth={2.5} />
                <Line type="monotone" dataKey="optimized" stroke="#2cc26b" strokeWidth={2} dot={false} strokeDasharray="4 4" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </Panel>

        <div className="agent-card">
          <div className="agent-glow" />
          <div className="agent-card-head"><span className="ai-badge"><Sparkles size={13} /> FINOPS AGENT</span><span className="live-tag">LIVE</span></div>
          <h2>I found {recs.length} optimization opportunit{recs.length === 1 ? "y" : "ies"}.</h2>
          <p>Correlated CloudWatch utilization, Cost Explorer spend, resource tags, and Terraform state.</p>
          <div className="agent-saving">
            <div><small>Potential savings</small><strong>{money(recs.reduce((a, r) => a + r.savings, 0))} <span>/ month</span></strong></div>
            <div className="risk-pills">
              <span>{recs.filter((r) => r.risk === "low").length} LOW</span>
              <span>{recs.filter((r) => r.risk === "medium").length} MEDIUM</span>
            </div>
          </div>
          <div className="agent-footer">
            <span><span className="pulse" /> Last analysis 4 min ago</span>
            <button onClick={() => navigate("Recommendations")}>View all <ChevronRight size={14} /></button>
          </div>
        </div>
      </div>

      <section className="section">
        <div className="section-title"><h2>Top optimization opportunities</h2>
          <button className="text-btn" onClick={() => navigate("Recommendations")}>View all <ChevronRight size={14} /></button>
        </div>
        <div className="rec-grid">
          {recs.map((r) => (
            <button className="rec-card" key={r.id} onClick={() => openRec(r)}>
              <div className="rec-top"><Badge risk={r.risk} /><strong>{money(r.savings)}<small>/mo</small></strong></div>
              <h3>{r.type} {r.type === "EBS" ? "unattached" : "underutilized"}</h3>
              <p>{r.name} · {r.resource}</p>
              <div className="resource-change"><span>{r.from}</span><ChevronRight size={14} /><b>{r.to}</b></div>
              <div className="rec-bottom"><span>Agent reasoning available</span><ChevronRight size={15} /></div>
            </button>
          ))}
          {recs.length === 0 && <div className="empty-inline">No pending recommendations right now.</div>}
        </div>
      </section>

      <div className="grid-2">
        <Panel title="Resource health" action={<button className="text-btn" onClick={() => navigate("Resources")}>View resources <ChevronRight size={14} /></button>}>
          <HealthRow label="Healthy" value={total - idle - underutilized} pct={77} />
          <HealthRow label="Underutilized" value={underutilized} pct={17} />
          <HealthRow label="Idle / unused" value={idle} pct={6} />
        </Panel>
        <Panel title="Recent agent action" action={<button className="text-btn" onClick={() => navigate("Audit Log")}>View audit log <ChevronRight size={14} /></button>}>
          <div className="activity-mini">
            {audit.slice(0, 5).map((a, i) => (
              <div className="activity-row" key={i}>
                <span className={`activity-icon ${a.result}`}><Check size={12} /></span>
                <div><b>{a.action} · {a.resource}</b><small>{a.actor} · {a.time}</small></div>
              </div>
            ))}
          </div>
        </Panel>
      </div>
    </>
  );
}

function HealthRow({ label, value, pct }) {
  return (
    <div className="health-row">
      <div><span>{label}</span><b>{value}</b></div>
      <div className="bar"><span style={{ width: `${pct}%` }} /></div>
    </div>
  );
}

function Recommendations({ recs, openRec }) {
  return (
    <>
      <PageHeader eyebrow="FINOPS / AGENT" title="Savings opportunities" subtitle="Every recommendation includes the evidence and reasoning behind it — nothing is a black box." />
      <div className="opportunity-summary">
        <div><small>Potential savings</small><strong>{money(recs.reduce((a, r) => a + r.savings, 0))}<span>/ month</span></strong></div>
        <div className="summary-tags">
          <span>{recs.length} opportunities</span>
          <span>{recs.filter((r) => r.risk === "low").length} low risk</span>
          <span>{recs.filter((r) => r.risk === "medium").length} medium</span>
        </div>
      </div>
      <div className="rec-grid large">
        {recs.map((r) => (
          <button className="rec-card" key={r.id} onClick={() => openRec(r)}>
            <div className="rec-top"><Badge risk={r.risk} /><strong>{money(r.savings)}<small>/mo</small></strong></div>
            <h3>{r.type} {r.type === "EBS" ? "unattached" : "underutilized"}</h3>
            <p>{r.name} · {r.resource}</p>
            <div className="resource-change"><span>{r.from}</span><ChevronRight size={14} /><b>{r.to}</b></div>
            <div className="rec-evidence">
              {r.cpu !== null && <span>CPU {r.cpu}%</span>}
              <span>Confidence {r.confidence}%</span>
            </div>
            <div className="rec-bottom"><span>Agent reasoning available</span><ChevronRight size={15} /></div>
          </button>
        ))}
        {recs.length === 0 && (
          <div className="empty-state"><Check size={22} />No pending recommendations. The agent will surface new findings on its next scheduled scan.</div>
        )}
      </div>
    </>
  );
}

function Resources({ navigate, openResource }) {
  const [query, setQuery] = useState("");
  const [type, setType] = useState("All");
  const types = ["All", "EC2", "EBS", "RDS", "Lambda"];
  const filtered = resourceList.filter((r) =>
    (type === "All" || r.type === type) &&
    `${r.name} ${r.id}`.toLowerCase().includes(query.toLowerCase())
  );
  return (
    <>
      <PageHeader eyebrow="INFRASTRUCTURE" title="Resources" subtitle="A unified inventory of your AWS estate and optimization state." action={<button className="secondary-btn"><RefreshCw size={16} /> Sync AWS</button>} />
      <div className="resource-tabs">
        {types.map((t) => (
          <span key={t} className={type === t ? "active" : ""} onClick={() => setType(t)}>
            {t} <b>{t === "All" ? resourceList.length : resourceList.filter((r) => r.type === t).length}</b>
          </span>
        ))}
      </div>
      <Panel title="Resource inventory" action={<div className="table-search"><Search size={14} /><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search resources…" /></div>}>
        <div className="table-wrap">
          <table>
            <thead><tr><th>Resource</th><th>Type</th><th>Region</th><th>Environment</th><th>Utilization</th><th>Cost</th><th>Status</th></tr></thead>
            <tbody>
              {filtered.map((r) => (
                <tr key={r.id} className={r.type === "EC2" ? "clickable" : ""} onClick={() => r.type === "EC2" && openResource(r)}>
                  <td><b>{r.name}</b><small>{r.id}</small></td>
                  <td><TypeIcon type={r.type} /> {r.type}</td>
                  <td>{r.region}</td>
                  <td><span className={`env ${r.env.toLowerCase()}`}>{r.env}</span></td>
                  <td>{r.cpu !== null ? `${r.cpu}% CPU` : "—"}</td>
                  <td>{money(r.cost)}</td>
                  <td><span className={`status ${r.status.toLowerCase()}`}>{r.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Panel>
    </>
  );
}

function TypeIcon({ type }) {
  const I = type === "EC2" ? Server : type === "RDS" ? Database : type === "EBS" ? HardDrive : Zap;
  return <I size={14} className="inline-icon" />;
}

function ResourceDetail({ resource, navigate, onGenerate }) {
  if (!resource) return null;
  return (
    <>
      <button className="back-btn" onClick={() => navigate("Resources")}><ChevronLeft size={14} /> Resources</button>
      <PageHeader
        eyebrow={`${resource.type} / ${resource.id}`} title={resource.name}
        subtitle={`${resource.instance} · ${resource.region} · ${resource.terraform ? "Terraform managed" : "Not Terraform managed"}`}
        action={<span className="running"><i />{resource.status}</span>}
      />
      <div className="metric-grid">
        <div className="metric card"><span>CPU utilization</span><strong>{resource.cpu}%</strong><small>14d average</small></div>
        <div className="metric card"><span>Network</span><strong>0.03 MB/s</strong><small>14d average</small></div>
        <div className="metric card"><span>Monthly cost</span><strong>{money(resource.cost)}</strong><small>current estimate</small></div>
        <div className="metric card"><span>Environment</span><strong>{resource.env}</strong><small>tag</small></div>
      </div>
      <div className="grid-2">
        <Panel title="CPU utilization — 14 days">
          <div className="chart">
            <ResponsiveContainer width="100%" height={230}>
              <LineChart data={cpuSeries14d}>
                <CartesianGrid stroke="#1a222b" vertical={false} />
                <XAxis dataKey="day" stroke="#5b6878" tickLine={false} axisLine={false} fontSize={11} />
                <YAxis domain={[0, 100]} stroke="#5b6878" tickLine={false} axisLine={false} fontSize={11} />
                <Tooltip contentStyle={{ background: "#161d26", border: "1px solid #212b35", borderRadius: 10, fontSize: 12 }} />
                <Line dataKey="cpu" stroke="#3fc1f0" strokeWidth={2.5} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </Panel>
        <section className="ai-analysis card">
          <div className="ai-head"><span className="ai-badge"><Sparkles size={13} /> AI ANALYSIS</span><span className="confidence">94% confidence</span></div>
          <h3>Resize <code>{resource.instance}</code> → smaller class</h3>
          <p>CPU has remained below 5% for 14 days. Network activity is negligible, the instance is tagged {resource.env}, and Terraform manages this resource.</p>
          <div className="ai-facts">
            <div><small>Estimated savings</small><b>₹1,240 / month</b></div>
            <div><small>Risk</small><b className="low-text">LOW</b></div>
            <div><small>Evidence</small><b>4 signals</b></div>
          </div>
          <button className="primary-btn full" onClick={() => onGenerate(resource)}><FileCode2 size={16} /> Generate Terraform plan</button>
        </section>
      </div>
    </>
  );
}

function TerraformCenter({ items, onApply }) {
  const managed = 47, drift = 2, pending = items.filter((i) => i.stage !== "done").length;
  const steps = ["Plan generated", "Policy validation passed", "Approval received", "Terraform apply", "AWS resource updated", "Savings verification scheduled"];
  return (
    <>
      <PageHeader eyebrow="INFRASTRUCTURE" title="Terraform Center" subtitle="Every agent-proposed change is a reviewable diff — nothing applies without human approval." />
      <div className="kpi-grid four">
        <KPI icon={Server} label="Managed resources" value={managed} />
        <KPI icon={AlertTriangle} label="Drift detected" value={drift} tone="up" />
        <KPI icon={ListChecks} label="Pending changes" value={pending} />
        <KPI icon={RefreshCw} label="Last plan" value="3m ago" />
      </div>
      {items.length === 0 && <div className="empty-state"><FileCode2 size={22} />No Terraform changes queued yet. Approve a recommendation to generate one.</div>}
      {items.map((item) => {
        const stageIndex = item.stage === "pending" ? 2 : item.stage === "applying" ? 4 : 6;
        return (
          <Panel key={item.id} title={`aws_${item.field === "delete" ? "ebs_volume" : "instance"}.${item.resource.replace(/[^a-z0-9]/gi, "_")}`} action={<Badge risk={item.risk} />}>
            <pre className="diff-box">
              <span className="remove">- {item.field} = "{item.from}"</span>{"\n"}
              <span className="add">+ {item.field} = "{item.to}"</span>
            </pre>
            <div className="tf-meta"><span>Reason <b>{item.reason}</b></span><span>Estimated savings <b>{money(item.savings)}/month</b></span></div>
            <div className="tf-foot">
              {item.stage === "pending" && <button className="primary-btn" onClick={() => onApply(item)}>Approve &amp; apply</button>}
              {item.stage === "applying" && <button className="secondary-btn" disabled>Applying…</button>}
              {item.stage === "done" && <span className="badge low"><i /> Applied</span>}
            </div>
            <div className="pipeline-inline">
              {steps.map((s, i) => (
                <div className={i < stageIndex ? "done" : i === stageIndex ? "current" : ""} key={s}>
                  <span>{i < stageIndex ? <Check size={12} /> : i + 1}</span>{s}
                </div>
              ))}
            </div>
          </Panel>
        );
      })}
    </>
  );
}

function AgentActivity() {
  const [lines, setLines] = useState([]);
  const [running, setRunning] = useState(false);
  const timeouts = useRef([]);

  const run = () => {
    timeouts.current.forEach(clearTimeout);
    setLines([]); setRunning(true);
    activityScript.forEach((l, i) => {
      const id = setTimeout(() => {
        setLines((prev) => [...prev, { ...l, time: new Date().toTimeString().slice(0, 8) }]);
        if (i === activityScript.length - 1) setRunning(false);
      }, i * 420);
      timeouts.current.push(id);
    });
  };

  useEffect(() => { run(); return () => timeouts.current.forEach(clearTimeout); }, []);

  return (
    <>
      <PageHeader eyebrow="AGENT" title="Agent activity" subtitle="A transparent execution trace of what the agent observed, reasoned about, and proposed." action={<button className="secondary-btn" onClick={run} disabled={running}><RefreshCw size={16} /> {running ? "Running…" : "Run again"}</button>} />
      <Panel title="Live execution trace" action={<span className="live"><i /> {running ? "LIVE" : "IDLE"}</span>}>
        <div className="timeline">
          {lines.length === 0 && <div className="empty-inline">Waiting for agent…</div>}
          {lines.map((l, i) => (
            <div className="timeline-row" key={i}>
              <time>{l.time}</time>
              <span className={`timeline-dot ${l.t === "ok" ? "success" : l.t === "warn" ? "warning" : l.t === "wait" ? "wait" : "info"}`} />
              <div><b>{l.text}</b></div>
            </div>
          ))}
        </div>
      </Panel>
    </>
  );
}

function Approvals({ recs, openRec }) {
  return (
    <>
      <PageHeader eyebrow="GOVERNANCE" title="Approvals" subtitle={`${recs.length} action${recs.length !== 1 ? "s" : ""} awaiting your decision.`} />
      <div className="approval-banner"><ShieldCheck size={20} /><div><b>Production changes require approval</b><span>Policy: production resize and destructive actions are never auto-applied.</span></div></div>
      {recs.length === 0 && <div className="empty-state"><Check size={22} />Nothing waiting on you right now.</div>}
      <div className="approval-grid">
        {recs.map((r) => (
          <div className="approval-card card" key={r.id}>
            <div className="rec-top"><Badge risk={r.risk} /><strong>{money(r.savings)}<small>/mo</small></strong></div>
            <h3>{r.type} · {r.name}</h3>
            <p>{r.from} → {r.to}</p>
            <div className="approval-meta"><span>Requested by <b>FinOps Agent</b></span><span>3 min ago</span></div>
            <button className="primary-btn full" onClick={() => openRec(r)}>Review &amp; decide</button>
          </div>
        ))}
      </div>
    </>
  );
}

function Forecast() {
  return (
    <>
      <PageHeader eyebrow="FINOPS" title="Cost forecast" subtitle="Projected spend versus current trajectory and optimization scenarios." />
      <div className="forecast-hero card">
        <div><small>Current month</small><strong>₹18,430</strong></div>
        <div className="forecast-arrow">→</div>
        <div><small>Forecast</small><strong>₹23,840</strong></div>
        <div className="budget"><small>Budget</small><strong>₹25,000</strong><span>95% projected</span></div>
      </div>
      <Panel title="Forecast trajectory">
        <div className="chart tall">
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={costSeries}>
              <CartesianGrid stroke="#1a222b" vertical={false} />
              <XAxis dataKey="month" stroke="#5b6878" tickLine={false} axisLine={false} fontSize={12} />
              <YAxis stroke="#5b6878" tickLine={false} axisLine={false} fontSize={12} />
              <Tooltip contentStyle={{ background: "#161d26", border: "1px solid #212b35", borderRadius: 10, fontSize: 12 }} />
              <Line dataKey="actual" stroke="#3fc1f0" strokeWidth={2.5} dot={false} />
              <Line dataKey="forecast" stroke="#8e7cf6" strokeWidth={2} strokeDasharray="5 5" dot={false} />
              <Line dataKey="optimized" stroke="#2cc26b" strokeWidth={2} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </Panel>
      <div className="insight">
        <Sparkles size={19} />
        <div><b>Agent insight</b><p>At the current spending rate, projected cost is ₹23,840. The agent identified ₹4,280/month in opportunities — if approved, modeled monthly cost drops to approximately ₹19,560.</p></div>
      </div>
    </>
  );
}

function AuditLog({ rows }) {
  return (
    <>
      <PageHeader eyebrow="GOVERNANCE" title="Audit log" subtitle="Every action the agent or a human took, in order." />
      <Panel title="Recent events">
        <div className="table-wrap">
          <table>
            <thead><tr><th>Time</th><th>Actor</th><th>Action</th><th>Resource</th><th>Result</th></tr></thead>
            <tbody>
              {rows.map((r, i) => (
                <tr key={i}>
                  <td className="mono">{r.time}</td>
                  <td><b>{r.actor}</b></td>
                  <td>{r.action}</td>
                  <td className="mono">{r.resource}</td>
                  <td><span className={`status ${r.result}`}>{r.result}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Panel>
    </>
  );
}

/* =====================================================================
   SIDEBAR / TOPBAR / SHELL
===================================================================== */

const NAV = [
  { title: "OVERVIEW", items: [["Dashboard", LayoutDashboard]] },
  { title: "FINOPS", items: [["Recommendations", TrendingDown], ["Forecast", TrendingUp]] },
  { title: "INFRASTRUCTURE", items: [["Resources", Server], ["Terraform Center", FileCode2]] },
  { title: "AGENT", items: [["Agent Activity", Activity], ["Approvals", ListChecks]] },
  { title: "RECORDS", items: [["Audit Log", Terminal]] },
];

function Sidebar({ page, navigate, mobileOpen, closeMobile, badges }) {
  return (
    <aside className={`sidebar ${mobileOpen ? "open" : ""}`}>
      <div className="brand">
        <div className="brand-mark"><Sparkles size={18} /></div>
        <div><strong>CostPilot</strong><span>FINOPS AGENT</span></div>
        <button className="mobile-close" onClick={closeMobile}><X size={18} /></button>
      </div>
      <nav>
        {NAV.map((g) => (
          <div className="nav-group" key={g.title}>
            <div className="nav-label">{g.title}</div>
            {g.items.map(([name, Icon]) => (
              <button key={name} className={`nav-item ${page === name ? "active" : ""}`} onClick={() => navigate(name)}>
                <Icon size={16} /><span>{name}</span>
                {badges[name] > 0 && <em>{badges[name]}</em>}
              </button>
            ))}
          </div>
        ))}
      </nav>
      <div className="agent-status"><span className="online-dot" /><div><b>Agent online</b><small>Monitoring AWS</small></div></div>
    </aside>
  );
}

function Select({ value, setValue, options }) {
  return (
    <label className="select">
      <select value={value} onChange={(e) => setValue(e.target.value)}>
        {options.map((o) => <option key={o}>{o}</option>)}
      </select>
      <ChevronDown size={13} />
    </label>
  );
}

function Topbar({ onMenu, onChat, account, setAccount, region, setRegion, period, setPeriod }) {
  return (
    <header className="topbar">
      <button className="menu-btn" onClick={onMenu}><Menu size={20} /></button>
      <div className="search-box"><Search size={16} /><input placeholder="Search resources…" /></div>
      <Select value={account} setValue={setAccount} options={["Production", "Development", "All accounts"]} />
      <Select value={region} setValue={setRegion} options={["ap-south-1", "us-east-1", "All regions"]} />
      <Select value={period} setValue={setPeriod} options={["Last 7 days", "Last 30 days", "Last 90 days"]} />
      <button className="icon-btn notification"><Bell size={17} /><span>3</span></button>
      <button className="chat-top" onClick={onChat}><Sparkles size={16} /> Copilot</button>
      <div className="avatar">A</div>
    </header>
  );
}

function Chat({ close }) {
  const [input, setInput] = useState("");
  const [msgs, setMsgs] = useState([
    { role: "agent", text: "EC2 spending increased 18% this month, mainly from i-0a83f2c1 running continuously in dev. I found 2 low-risk opportunities worth ₹1,860/month." },
  ]);
  const send = () => {
    if (!input.trim()) return;
    setMsgs((m) => [...m, { role: "user", text: input }]);
    setInput("");
    setTimeout(() => setMsgs((m) => [...m, { role: "agent", text: "Check the Recommendations tab — every finding there includes the evidence behind it, so you can verify before approving anything." }]), 600);
  };
  return (
    <div className="copilot-backdrop" onClick={close}>
      <aside className="copilot" onClick={(e) => e.stopPropagation()}>
        <div className="copilot-head"><div><span className="ai-badge"><Sparkles size={13} /> FINOPS AGENT</span><h2>Copilot</h2></div><button className="icon-btn" onClick={close}><X size={18} /></button></div>
        <div className="chat-messages">
          {msgs.map((m, i) => (
            <div className={`message ${m.role}`} key={i}>
              <span>{m.role === "agent" ? <Bot size={15} /> : <UserRound size={15} />}</span>
              <p>{m.text}</p>
            </div>
          ))}
        </div>
        <div className="chat-input">
          <input value={input} onChange={(e) => setInput(e.target.value)} onKeyDown={(e) => e.key === "Enter" && send()} placeholder="Ask anything about your cloud…" />
          <button onClick={send}><ChevronRight size={16} /></button>
        </div>
      </aside>
    </div>
  );
}

/* =====================================================================
   APP — this is where the state loop lives. Every page reads from the
   same recs / tfItems / audit state, so an approval anywhere in the
   app is reflected everywhere else immediately.
===================================================================== */

function App() {
  const [page, setPage] = useState("Dashboard");
  const [mobileOpen, setMobileOpen] = useState(false);
  const [chatOpen, setChatOpen] = useState(false);
  const [drawerRec, setDrawerRec] = useState(null);
  const [selectedResource, setSelectedResource] = useState(null);
  const [toast, setToast] = useState("");

  const [account, setAccount] = useState("Production");
  const [region, setRegion] = useState("ap-south-1");
  const [period, setPeriod] = useState("Last 30 days");

  const [recs, setRecs] = useState(initialRecs);
  const [tfItems, setTfItems] = useState([]);
  const [audit, setAudit] = useState(auditSeed);

  const showToast = (msg) => { setToast(msg); setTimeout(() => setToast(""), 3000); };
  const addAudit = (row) => setAudit((a) => [{ ...row, time: new Date().toTimeString().slice(0, 8) }, ...a]);

  const navigate = (name) => { setPage(name); setMobileOpen(false); };

  const handleApprove = (r) => {
    setRecs((prev) => prev.filter((x) => x.id !== r.id));
    setTfItems((prev) => [...prev, {
      id: r.id, resource: r.resource, from: r.from, to: r.to,
      field: r.type === "EBS" ? "delete" : "instance_type",
      reason: r.type === "EBS" ? "Unattached for 21 days" : "Sustained low utilization",
      risk: r.risk, savings: r.savings, stage: "pending",
    }]);
    addAudit({ actor: "Aarish", action: "Approved recommendation", resource: r.resource, result: "success" });
    showToast(`Sent ${r.resource} to Terraform Center`);
    setDrawerRec(null);
    navigate("Terraform Center");
  };

  const handleReject = (r) => {
    setRecs((prev) => prev.filter((x) => x.id !== r.id));
    addAudit({ actor: "Aarish", action: "Dismissed recommendation", resource: r.resource, result: "rejected" });
    showToast(`Dismissed recommendation for ${r.resource}`);
    setDrawerRec(null);
  };

  const handleApply = (item) => {
    setTfItems((prev) => prev.map((t) => (t.id === item.id ? { ...t, stage: "applying" } : t)));
    addAudit({ actor: "Terraform", action: "terraform plan", resource: item.resource, result: "success" });
    setTimeout(() => {
      setTfItems((prev) => prev.map((t) => (t.id === item.id ? { ...t, stage: "done" } : t)));
      addAudit({ actor: "Terraform", action: "terraform apply", resource: item.resource, result: "success" });
      addAudit({ actor: "FinOps Agent", action: "Verify savings", resource: item.resource, result: "pending" });
      showToast(`${item.resource} applied successfully`);
    }, 1600);
  };

  const handleGenerateFromResource = (resource) => {
    const exists = tfItems.some((t) => t.resource === resource.id);
    if (!exists) {
      setTfItems((prev) => [...prev, {
        id: resource.id, resource: resource.id, from: resource.instance, to: "right-sized class",
        field: "instance_type", reason: "Generated from resource detail analysis",
        risk: "low", savings: 1240, stage: "pending",
      }]);
      addAudit({ actor: "FinOps Agent", action: "Generate Terraform plan", resource: resource.id, result: "success" });
    }
    showToast(`Terraform plan ready for ${resource.id}`);
    navigate("Terraform Center");
  };

  const badges = {
    Recommendations: recs.length,
    Approvals: recs.length,
    "Terraform Center": tfItems.filter((t) => t.stage === "pending").length,
  };

  return (
    <div className="app-shell">
      <Sidebar page={page} navigate={navigate} mobileOpen={mobileOpen} closeMobile={() => setMobileOpen(false)} badges={badges} />
      <div className="main-shell">
        <Topbar onMenu={() => setMobileOpen(true)} onChat={() => setChatOpen(true)}
          account={account} setAccount={setAccount} region={region} setRegion={setRegion} period={period} setPeriod={setPeriod} />
        <main className="page">
          {page === "Dashboard" && <Dashboard recs={recs} tfItems={tfItems} audit={audit} navigate={navigate} openRec={setDrawerRec} />}
          {page === "Recommendations" && <Recommendations recs={recs} openRec={setDrawerRec} />}
          {page === "Resources" && <Resources navigate={navigate} openResource={(r) => { setSelectedResource(r); navigate("Resource Detail"); }} />}
          {page === "Resource Detail" && <ResourceDetail resource={selectedResource} navigate={navigate} onGenerate={handleGenerateFromResource} />}
          {page === "Terraform Center" && <TerraformCenter items={tfItems} onApply={handleApply} />}
          {page === "Agent Activity" && <AgentActivity />}
          {page === "Approvals" && <Approvals recs={recs} openRec={setDrawerRec} />}
          {page === "Forecast" && <Forecast />}
          {page === "Audit Log" && <AuditLog rows={audit} />}
        </main>
      </div>

      <button className="copilot-fab" onClick={() => setChatOpen(true)}><Sparkles size={17} /> Ask FinOps Agent</button>
      {chatOpen && <Chat close={() => setChatOpen(false)} />}
      {drawerRec && <RecommendationDrawer rec={drawerRec} close={() => setDrawerRec(null)} onApprove={handleApprove} onReject={handleReject} />}
      <Toast message={toast} />
    </div>
  );
}

createRoot(document.getElementById("root")).render(<App />);
