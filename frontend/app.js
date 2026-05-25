const params = new URLSearchParams(window.location.search);
const DEFAULT_CORE = params.get("coreApi") || window.localStorage.getItem("medicoreCoreApiBase") || "https://hospital-core.onrender.com";
const DEFAULT_APPT = params.get("apptApi") || window.localStorage.getItem("medicoreApptApiBase") || "https://hospital-appointment.onrender.com";
const CORE_API_BASE = params.get("api") || DEFAULT_CORE;
const APPT_API_BASE = params.get("api") || DEFAULT_APPT;

const api = {
    patients: `${CORE_API_BASE}/api/patients`,
    doctors: `${CORE_API_BASE}/api/doctors`,
    appointments: `${APPT_API_BASE}/api/appointments`
};

const state = {
    page: "dashboard",
    patients: [],
    doctors: [],
    appointments: [],
    filters: {
        patients: { search: "", status: "All" },
        doctors: { search: "" },
        appointments: { search: "", date: "All Dates" }
    }
};

const patientStatuses = ["ADMITTED", "CRITICAL", "SURGERY", "OBSERVATION", "DISCHARGED"];
const appointmentStatuses = ["SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"];
const departments = [
    ["Cardiology", "var(--blue)", "var(--blue-soft)"],
    ["ICU", "var(--red)", "var(--red-soft)"],
    ["Orthopedics", "var(--amber)", "var(--amber-soft)"],
    ["Neurology", "var(--violet)", "var(--violet-soft)"],
    ["General", "var(--green)", "var(--green-soft)"],
    ["Pulmonology", "var(--teal)", "var(--teal-soft)"],
    ["Gynecology", "#993556", "#fbeaf0"],
    ["Ophthalmology", "var(--blue)", "var(--blue-soft)"]
];
const pharmacy = [
    ["Paracetamol 500mg", "Analgesic", "840 units", "green"],
    ["Amoxicillin 250mg", "Antibiotic", "320 units", "blue"],
    ["Metformin 500mg", "Antidiabetic", "512 units", "violet"],
    ["Atenolol 50mg", "Beta-blocker", "275 units", "amber"],
    ["Omeprazole 20mg", "PPI", "430 units", "teal"],
    ["Aspirin 75mg", "Antiplatelet", "980 units", "red"],
    ["Amlodipine 5mg", "CCB", "190 units", "violet"],
    ["Cetirizine 10mg", "Antihistamine", "650 units", "blue"]
];

const app = document.querySelector("#app");
const dialog = document.querySelector("#recordDialog");
const dialogTitle = document.querySelector("#dialogTitle");
const form = document.querySelector("#recordForm");
const formFields = document.querySelector("#formFields");
const toast = document.querySelector("#toast");
let activeForm = null;

document.querySelectorAll(".nav-item").forEach((button) => {
    button.addEventListener("click", () => setPage(button.dataset.page));
});
document.querySelector("#mobileNav").addEventListener("change", (event) => setPage(event.target.value));
document.querySelector("#closeDialog").addEventListener("click", () => dialog.close());
document.querySelector("#cancelDialog").addEventListener("click", () => dialog.close());
form.addEventListener("submit", saveRecord);

loadData();

async function loadData() {
    app.innerHTML = `<div class="empty">Loading hospital records...</div>`;
    try {
        const [patients, doctors, appointments] = await Promise.all([
            getJson(api.patients),
            getJson(api.doctors),
            getJson(api.appointments)
        ]);
        state.patients = patients;
        state.doctors = doctors;
        state.appointments = appointments;
        render();
    } catch (error) {
        app.innerHTML = `<div class="empty">Could not reach the Spring Boot API. Start the backend and refresh this page.</div>`;
        console.error(error);
    }
}

async function getJson(url) {
    const response = await fetch(url);
    if (!response.ok) throw new Error(`${url} returned ${response.status}`);
    return response.json();
}

function setPage(page) {
    state.page = page;
    document.querySelectorAll(".nav-item").forEach((button) => {
        button.classList.toggle("active", button.dataset.page === page);
    });
    document.querySelector("#mobileNav").value = page;
    render();
}

function render() {
    const pages = {
        dashboard: renderDashboard,
        patients: renderPatients,
        appointments: renderAppointments,
        doctors: renderDoctors,
        departments: renderDepartments,
        pharmacy: renderPharmacy,
        reports: renderReports
    };
    pages[state.page]();
}

function renderDashboard() {
    const today = "2024-05-21";
    const recentPatients = state.patients.slice(0, 5);
    const todayAppointments = state.appointments.filter((item) => item.date === today);

    app.innerHTML = `
        ${pageHead("Dashboard Overview", `Today: ${new Date().toLocaleDateString(undefined, { dateStyle: "medium" })}`)}
        <div class="stats-grid">
            ${statCard("Total Patients", state.patients.length, "+6.2% this month", "blue")}
            ${statCard("Admitted", countPatients("ADMITTED"), "Currently in ward", "green")}
            ${statCard("Today's Appointments", todayAppointments.length, "Scheduled today", "amber")}
            ${statCard("Critical Cases", countPatients("CRITICAL"), "Needs attention", "red")}
        </div>
        <div class="split">
            <section class="panel">
                <div class="panel-head"><h3>Recent Patients</h3></div>
                ${recentPatients.map(patientRow).join("") || empty("No patients yet.")}
            </section>
            <section class="panel">
                <div class="panel-head"><h3>Today's Appointments</h3></div>
                ${todayAppointments.map(appointmentRow).join("") || empty("No appointments today.")}
            </section>
        </div>
    `;
}

function renderPatients() {
    const filtered = state.patients.filter((patient) => {
        const search = state.filters.patients.search.toLowerCase();
        const matchesSearch = !search || [patient.name, patient.ward, patient.diagnosis, patient.doctor]
            .some((value) => String(value || "").toLowerCase().includes(search));
        const matchesStatus = state.filters.patients.status === "All" || patient.status === state.filters.patients.status;
        return matchesSearch && matchesStatus;
    });

    app.innerHTML = `
        ${pageHead("Patient Records", "")}
        <div class="toolbar">
            <input class="search" id="patientSearch" value="${escapeHtml(state.filters.patients.search)}" placeholder="Search patients">
            <select class="filter" id="patientStatus">
                ${["All", ...patientStatuses].map((status) => option(status, state.filters.patients.status, labelize(status))).join("")}
            </select>
            <button class="primary" id="addPatient" type="button">Add Patient</button>
        </div>
        ${table(["ID", "Patient Name", "Age", "Ward", "Diagnosis", "Doctor", "Admitted", "Status", "Actions"], filtered.map((patient) => `
            <tr>
                <td>${patient.id}</td>
                <td>${escapeHtml(patient.name)}</td>
                <td>${patient.age ?? ""}</td>
                <td>${escapeHtml(patient.ward)}</td>
                <td>${escapeHtml(patient.diagnosis)}</td>
                <td>${escapeHtml(patient.doctor)}</td>
                <td>${escapeHtml(patient.admissionDate)}</td>
                <td>${statusPill(patient.status)}</td>
                <td>${actions("patients", patient.id)}</td>
            </tr>
        `))}
    `;
    bindFilter("patientSearch", (value) => state.filters.patients.search = value);
    bindSelect("patientStatus", (value) => state.filters.patients.status = value);
    document.querySelector("#addPatient").addEventListener("click", () => openRecordDialog("patients"));
    bindActions("patients");
}

function renderDoctors() {
    const search = state.filters.doctors.search.toLowerCase();
    const filtered = state.doctors.filter((doctor) => !search || [doctor.name, doctor.specialization, doctor.email]
        .some((value) => String(value || "").toLowerCase().includes(search)));

    app.innerHTML = `
        ${pageHead("Doctors", "")}
        <div class="toolbar">
            <input class="search" id="doctorSearch" value="${escapeHtml(state.filters.doctors.search)}" placeholder="Search doctors">
            <span></span>
            <button class="primary" id="addDoctor" type="button">Add Doctor</button>
        </div>
        ${table(["ID", "Doctor Name", "Specialization", "Phone", "Email", "Patients", "Availability", "Actions"], filtered.map((doctor) => `
            <tr>
                <td>${doctor.id}</td>
                <td>${escapeHtml(doctor.name)}</td>
                <td>${escapeHtml(doctor.specialization)}</td>
                <td>${escapeHtml(doctor.phone)}</td>
                <td>${escapeHtml(doctor.email)}</td>
                <td>${doctor.patientsCount ?? 0}</td>
                <td>${escapeHtml(doctor.availability)}</td>
                <td>${actions("doctors", doctor.id)}</td>
            </tr>
        `))}
    `;
    bindFilter("doctorSearch", (value) => state.filters.doctors.search = value);
    document.querySelector("#addDoctor").addEventListener("click", () => openRecordDialog("doctors"));
    bindActions("doctors");
}

function renderAppointments() {
    const today = "2024-05-21";
    const filtered = state.appointments.filter((appointment) => {
        const search = state.filters.appointments.search.toLowerCase();
        const matchesSearch = !search || [appointment.patientName, appointment.doctorName, appointment.department]
            .some((value) => String(value || "").toLowerCase().includes(search));
        const dateFilter = state.filters.appointments.date;
        const matchesDate = dateFilter === "All Dates" || (dateFilter === "Today" && appointment.date === today) ||
            (dateFilter === "Upcoming" && appointment.date > today);
        return matchesSearch && matchesDate;
    });

    app.innerHTML = `
        ${pageHead("Appointments", "")}
        <div class="toolbar">
            <input class="search" id="appointmentSearch" value="${escapeHtml(state.filters.appointments.search)}" placeholder="Search appointments">
            <select class="filter" id="appointmentDate">
                ${["All Dates", "Today", "Upcoming"].map((item) => option(item, state.filters.appointments.date, item)).join("")}
            </select>
            <button class="primary" id="addAppointment" type="button">New Appointment</button>
        </div>
        ${table(["ID", "Patient", "Doctor", "Department", "Date", "Time", "Status", "Actions"], filtered.map((appointment) => `
            <tr>
                <td>${appointment.id}</td>
                <td>${escapeHtml(appointment.patientName)}</td>
                <td>${escapeHtml(appointment.doctorName)}</td>
                <td>${escapeHtml(appointment.department)}</td>
                <td>${escapeHtml(appointment.date)}</td>
                <td>${escapeHtml(appointment.time)}</td>
                <td>${statusPill(appointment.status)}</td>
                <td>${actions("appointments", appointment.id)}</td>
            </tr>
        `))}
    `;
    bindFilter("appointmentSearch", (value) => state.filters.appointments.search = value);
    bindSelect("appointmentDate", (value) => state.filters.appointments.date = value);
    document.querySelector("#addAppointment").addEventListener("click", () => openRecordDialog("appointments"));
    bindActions("appointments");
}

function renderDepartments() {
    app.innerHTML = `
        ${pageHead("Departments", "Patient distribution across hospital departments")}
        <div class="card-grid">
            ${departments.map(([name, accent, bg]) => {
                const patients = state.patients.filter((patient) => equalsText(patient.ward, name)).length;
                const doctors = state.doctors.filter((doctor) => String(doctor.specialization || "").toLowerCase().includes(name.toLowerCase())).length;
                const capacity = Math.min(100, Math.round((patients / 15) * 100));
                return `
                    <section class="department">
                        <h3>${name}</h3>
                        <div class="counts">
                            <div><strong style="color:${accent}">${patients}</strong><span class="muted">patients</span></div>
                            <div><strong>${doctors}</strong><span class="muted">doctors</span></div>
                        </div>
                        <div class="capacity" style="background:${bg}"><span style="width:${capacity}%;background:${accent}"></span></div>
                        <span class="muted">${capacity}% capacity</span>
                    </section>
                `;
            }).join("")}
        </div>
    `;
}

function renderPharmacy() {
    app.innerHTML = `
        ${pageHead("Pharmacy", "")}
        <div class="card-grid">
            ${pharmacy.map(([name, category, stock, color]) => `
                <section class="med-card">
                    <h3>${name}</h3>
                    <p><span class="pill ${color}">${category}</span></p>
                    <p class="muted">${stock}</p>
                </section>
            `).join("")}
        </div>
    `;
}

function renderReports() {
    app.innerHTML = `
        ${pageHead("Reports & Analytics", "")}
        <div class="chart-grid">
            <section class="panel chart-box">
                <div class="panel-head"><h3>Patient Status Breakdown</h3></div>
                ${donutChart()}
            </section>
            <section class="panel chart-box">
                <div class="panel-head"><h3>Patients per Ward</h3></div>
                ${barChart()}
            </section>
        </div>
        <section class="panel" style="margin-top:14px">
            <div class="panel-head"><h3>Summary Statistics</h3></div>
            <div class="summary-grid">
                ${summaryItem("Total Patients Registered", state.patients.length)}
                ${summaryItem("Total Doctors on Staff", state.doctors.length)}
                ${summaryItem("Total Appointments", state.appointments.length)}
                ${summaryItem("Admitted Patients", countPatients("ADMITTED"))}
                ${summaryItem("Critical Cases", countPatients("CRITICAL"))}
                ${summaryItem("Pending Surgeries", countPatients("SURGERY"))}
            </div>
        </section>
    `;
}

function openRecordDialog(type, id = null) {
    const existing = id == null ? null : state[type].find((item) => Number(item.id) === Number(id));
    activeForm = { type, id };
    dialogTitle.textContent = `${existing ? "Edit" : type === "appointments" ? "New" : "Add"} ${singular(type)}`;
    formFields.innerHTML = getFields(type, existing).map(fieldTemplate).join("");
    dialog.showModal();
}

function getFields(type, record) {
    const commonId = { name: "id", label: "ID", type: "number", value: record?.id ?? nextId(state[type]), readonly: true };
    if (type === "patients") {
        return [
            commonId,
            { name: "name", label: "Name", value: record?.name ?? "", required: true },
            { name: "age", label: "Age", type: "number", value: record?.age ?? "", required: true },
            { name: "ward", label: "Ward", value: record?.ward ?? "", required: true },
            { name: "diagnosis", label: "Diagnosis", value: record?.diagnosis ?? "", required: true },
            { name: "doctor", label: "Doctor", value: record?.doctor ?? "", required: true },
            { name: "admissionDate", label: "Admitted", type: "date", value: record?.admissionDate ?? "2024-05-21", required: true },
            { name: "status", label: "Status", options: patientStatuses, value: record?.status ?? "ADMITTED" }
        ];
    }
    if (type === "doctors") {
        return [
            commonId,
            { name: "name", label: "Name", value: record?.name ?? "Dr. ", required: true },
            { name: "specialization", label: "Specialization", value: record?.specialization ?? "", required: true },
            { name: "phone", label: "Phone", value: record?.phone ?? "", required: true },
            { name: "email", label: "Email", type: "email", value: record?.email ?? "", required: true },
            { name: "patientsCount", label: "Patients", type: "number", value: record?.patientsCount ?? 0 },
            { name: "availability", label: "Availability", value: record?.availability ?? "Mon-Fri", required: true }
        ];
    }
    return [
        commonId,
        { name: "patientName", label: "Patient", value: record?.patientName ?? "", required: true },
        { name: "doctorName", label: "Doctor", value: record?.doctorName ?? "", required: true },
        { name: "department", label: "Department", value: record?.department ?? "", required: true },
        { name: "date", label: "Date", type: "date", value: record?.date ?? "2024-05-21", required: true },
        { name: "time", label: "Time", type: "time", value: record?.time ?? "09:00", required: true },
        { name: "status", label: "Status", options: appointmentStatuses, value: record?.status ?? "SCHEDULED" }
    ];
}

async function saveRecord(event) {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(form).entries());
    payload.id = Number(payload.id);
    if (payload.age) payload.age = Number(payload.age);
    if (payload.patientsCount) payload.patientsCount = Number(payload.patientsCount);

    const { type, id } = activeForm;
    const url = id == null ? api[type] : `${api[type]}/${id}`;
    const method = id == null ? "POST" : "PUT";
    const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });
    if (!response.ok) {
        showToast("Save failed");
        return;
    }
    const saved = await response.json();
    const index = state[type].findIndex((item) => Number(item.id) === Number(saved.id));
    if (index >= 0) state[type][index] = saved;
    else state[type].push(saved);
    dialog.close();
    render();
    showToast(`${singular(type)} saved`);
}

async function deleteRecord(type, id) {
    const record = state[type].find((item) => Number(item.id) === Number(id));
    const label = record?.name || record?.patientName || `#${id}`;
    if (!confirm(`Remove ${label}?`)) return;
    const response = await fetch(`${api[type]}/${id}`, { method: "DELETE" });
    if (!response.ok) {
        showToast("Delete failed");
        return;
    }
    state[type] = state[type].filter((item) => Number(item.id) !== Number(id));
    render();
    showToast(`${singular(type)} removed`);
}

function bindActions(type) {
    document.querySelectorAll("[data-action='edit']").forEach((button) => {
        button.addEventListener("click", () => openRecordDialog(type, button.dataset.id));
    });
    document.querySelectorAll("[data-action='delete']").forEach((button) => {
        button.addEventListener("click", () => deleteRecord(type, button.dataset.id));
    });
}

function bindFilter(id, setter) {
    const input = document.querySelector(`#${id}`);
    input.addEventListener("input", () => {
        setter(input.value);
        render();
        input.focus();
        input.setSelectionRange(input.value.length, input.value.length);
    });
}

function bindSelect(id, setter) {
    document.querySelector(`#${id}`).addEventListener("change", (event) => {
        setter(event.target.value);
        render();
    });
}

function pageHead(title, subtitle) {
    return `
        <header class="page-head">
            <div>
                <h1>${title}</h1>
                ${subtitle ? `<p class="muted">${subtitle}</p>` : ""}
            </div>
        </header>
    `;
}

function statCard(label, value, sub, color) {
    return `<section class="stat ${color}"><span>${label}</span><strong>${value}</strong><small>${sub}</small></section>`;
}

function patientRow(patient) {
    return `
        <div class="list-row">
            <span class="avatar">${initials(patient.name)}</span>
            <div><strong>${escapeHtml(patient.name)}</strong><p class="muted">${escapeHtml(patient.ward)} - ${escapeHtml(patient.diagnosis)}</p></div>
            ${statusPill(patient.status)}
        </div>
    `;
}

function appointmentRow(appointment) {
    return `
        <div class="list-row">
            <strong style="color:var(--blue)">${escapeHtml(appointment.time)}</strong>
            <div><strong>${escapeHtml(appointment.patientName)}</strong><p class="muted">${escapeHtml(appointment.doctorName)} - ${escapeHtml(appointment.department)}</p></div>
            ${statusPill(appointment.status)}
        </div>
    `;
}

function table(headers, rows) {
    return `
        <div class="table-wrap">
            <table>
                <thead><tr>${headers.map((header) => `<th>${header}</th>`).join("")}</tr></thead>
                <tbody>${rows.join("") || `<tr><td colspan="${headers.length}" class="empty">No records found.</td></tr>`}</tbody>
            </table>
        </div>
    `;
}

function actions(type, id) {
    return `
        <div class="actions">
            <button class="small-button" data-action="edit" data-id="${id}" type="button">Edit</button>
            <button class="danger" data-action="delete" data-id="${id}" type="button">Delete</button>
        </div>
    `;
}

function statusPill(status) {
    const color = {
        ADMITTED: "blue",
        SCHEDULED: "blue",
        DISCHARGED: "green",
        COMPLETED: "green",
        CRITICAL: "red",
        CANCELLED: "red",
        SURGERY: "amber",
        IN_PROGRESS: "amber",
        OBSERVATION: "violet"
    }[status] || "teal";
    return `<span class="pill ${color}">${labelize(status)}</span>`;
}

function donutChart() {
    const data = patientStatuses.map((status) => [status, countPatients(status)]);
    const total = state.patients.length || 1;
    const colors = ["#185fa5", "#9d2d2d", "#7a4a10", "#5149a4", "#356c1f"];
    let offset = 25;
    const rings = data.map(([status, count], index) => {
        const amount = count / total * 100;
        const ring = `<circle r="72" cx="105" cy="105" pathLength="100" fill="transparent" stroke="${colors[index]}" stroke-width="34" stroke-dasharray="${amount} ${100 - amount}" stroke-dashoffset="${offset}" />`;
        offset -= amount;
        return ring;
    }).join("");
    const legend = data.map(([status, count], index) => `
        <g transform="translate(220 ${42 + index * 29})">
            <rect width="12" height="12" rx="3" fill="${colors[index]}"></rect>
            <text x="18" y="10" font-size="13" fill="#1d2329">${labelize(status)}</text>
            <text x="18" y="23" font-size="11" fill="#69727d">${count} patients</text>
        </g>
    `).join("");
    return `
        <svg viewBox="0 0 390 230" role="img" aria-label="Patient status breakdown">
            <g transform="rotate(-90 105 105)">${rings}</g>
            <circle cx="105" cy="105" r="48" fill="#fff"></circle>
            <text x="105" y="106" text-anchor="middle" font-size="24" font-weight="800" fill="#1d2329">${state.patients.length}</text>
            <text x="105" y="124" text-anchor="middle" font-size="12" fill="#69727d">patients</text>
            ${legend}
        </svg>
    `;
}

function barChart() {
    const counts = {};
    state.patients.forEach((patient) => counts[patient.ward] = (counts[patient.ward] || 0) + 1);
    const entries = Object.entries(counts).sort(([a], [b]) => a.localeCompare(b));
    const max = Math.max(1, ...entries.map(([, count]) => count));
    const colors = ["#185fa5", "#356c1f", "#9d2d2d", "#7a4a10", "#5149a4", "#0f6e56", "#993556"];
    const bars = entries.map(([ward, count], index) => {
        const height = Math.max(8, count / max * 142);
        const x = 32 + index * 44;
        return `
            <rect x="${x}" y="${176 - height}" width="28" height="${height}" rx="5" fill="${colors[index % colors.length]}"></rect>
            <text x="${x + 14}" y="${166 - height}" text-anchor="middle" font-size="12" font-weight="800">${count}</text>
            <text x="${x + 14}" y="196" text-anchor="middle" font-size="10" fill="#69727d">${escapeHtml(ward).slice(0, 7)}</text>
        `;
    }).join("");
    return `
        <svg viewBox="0 0 390 230" role="img" aria-label="Patients per ward">
            <line x1="20" x2="370" y1="178" y2="178" stroke="#dedbd2"></line>
            ${bars || `<text x="20" y="40" fill="#69727d">No ward data yet</text>`}
        </svg>
    `;
}

function fieldTemplate(field) {
    const attrs = [
        `name="${field.name}"`,
        `id="field_${field.name}"`,
        field.required ? "required" : "",
        field.readonly ? "readonly" : ""
    ].filter(Boolean).join(" ");
    if (field.options) {
        return `
            <div class="field">
                <label for="field_${field.name}">${field.label}</label>
                <select ${attrs}>${field.options.map((item) => option(item, field.value, labelize(item))).join("")}</select>
            </div>
        `;
    }
    return `
        <div class="field">
            <label for="field_${field.name}">${field.label}</label>
            <input ${attrs} type="${field.type || "text"}" value="${escapeHtml(field.value)}">
        </div>
    `;
}

function option(value, selected, label) {
    return `<option value="${escapeHtml(value)}" ${value === selected ? "selected" : ""}>${escapeHtml(label)}</option>`;
}

function summaryItem(label, value) {
    return `<div class="summary-item"><span class="muted">${label}</span><strong>${value}</strong></div>`;
}

function empty(message) {
    return `<p class="empty">${message}</p>`;
}

function countPatients(status) {
    return state.patients.filter((patient) => patient.status === status).length;
}

function nextId(items) {
    return Math.max(0, ...items.map((item) => Number(item.id) || 0)) + 1;
}

function singular(type) {
    return { patients: "Patient", doctors: "Doctor", appointments: "Appointment" }[type];
}

function labelize(value) {
    return String(value || "").toLowerCase().split("_").map((part) => part.charAt(0).toUpperCase() + part.slice(1)).join(" ");
}

function initials(name) {
    const parts = String(name || "").trim().split(/\s+/);
    return ((parts[0]?.[0] || "P") + (parts.at(-1)?.[0] || "")).toUpperCase();
}

function equalsText(left, right) {
    return String(left || "").toLowerCase() === String(right || "").toLowerCase();
}

function showToast(message) {
    toast.textContent = message;
    toast.classList.add("show");
    setTimeout(() => toast.classList.remove("show"), 2200);
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, (char) => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    }[char]));
}
