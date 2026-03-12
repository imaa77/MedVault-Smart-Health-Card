<?php
include("connection.php");

if (!isset($_GET['id'])) die("Invalid QR");

$public_id = trim($_GET['id']);

$sql = "SELECT id, name, emergency_name, emergency_email, emergency_phone 
        FROM user_details 
        WHERE public_id=?";

$stmt = $con->prepare($sql);
$stmt->bind_param("s", $public_id);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows == 0) die("User not found");

$user = $res->fetch_assoc();

$name = $user['name'];
$emergency_name = $user['emergency_name'];
$emergency_email = $user['emergency_email'];
$emergency_phone = $user['emergency_phone'];
?>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Emergency Access – MedVault</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="MedVault secure emergency contact access portal">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

<style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

    :root {
        --red: #C0392B;
        --red-dark: #9b2d22;
        --red-light: #fdf2f1;
        --red-mid: #f5c6c0;
        --blue: #0077B6;
        --border: #e2e8f0;
        --text: #1a202c;
        --text-muted: #64748b;
        --bg: #f7f0f0;
        --white: #ffffff;
        --radius: 12px;
        --shadow: 0 4px 24px rgba(0,0,0,0.07), 0 1px 4px rgba(0,0,0,0.05);
    }

    body {
        font-family: 'Inter', sans-serif;
        background: var(--bg);
        min-height: 100vh;
        display: flex;
        align-items: flex-start;
        justify-content: center;
        padding: 40px 16px;
        color: var(--text);
    }

    .wrapper { width: 100%; max-width: 500px; }

    .brand { display: flex; align-items: center; justify-content: center; margin-bottom: 20px; }
    .brand img { height: 80px; width: auto; object-fit: contain; }

    .card {
        background: var(--white);
        border-radius: var(--radius);
        box-shadow: var(--shadow);
        border: 1px solid var(--border);
        overflow: hidden;
    }

    /* Header */
    .card-header {
        padding: 20px 24px;
        border-bottom: 1px solid var(--border);
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .header-left { display: flex; align-items: center; gap: 12px; }

    .header-icon {
        width: 40px; height: 40px;
        background: var(--red-light);
        border-radius: 10px;
        display: flex; align-items: center; justify-content: center;
    }

    .header-icon svg {
        width: 20px; height: 20px;
        stroke: var(--red); fill: none;
        stroke-width: 1.7; stroke-linecap: round; stroke-linejoin: round;
    }

    .header-title { font-size: 15px; font-weight: 600; letter-spacing: -0.2px; }
    .header-sub { font-size: 12px; color: var(--text-muted); margin-top: 2px; }

    .badge {
        font-size: 11px; font-weight: 500;
        color: var(--red);
        background: var(--red-light);
        border: 1px solid var(--red-mid);
        padding: 3px 10px;
        border-radius: 20px;
    }

    /* Alert banner */
    .alert-bar {
        background: #fff5f5;
        border-bottom: 1px solid #fecaca;
        padding: 10px 24px;
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 12px;
        color: #b91c1c;
        font-weight: 500;
    }

    .alert-bar svg {
        width: 15px; height: 15px;
        stroke: #b91c1c; fill: none;
        stroke-width: 2; stroke-linecap: round; stroke-linejoin: round;
        flex-shrink: 0;
    }

    /* Patient row */
    .patient-row {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px 24px;
        background: #fafbfc;
        border-bottom: 1px solid var(--border);
    }

    .patient-avatar {
        width: 38px; height: 38px;
        background: var(--red-light);
        border-radius: 50%;
        display: flex; align-items: center; justify-content: center;
        flex-shrink: 0;
    }

    .patient-avatar svg {
        width: 18px; height: 18px;
        stroke: var(--red); fill: none;
        stroke-width: 1.7; stroke-linecap: round; stroke-linejoin: round;
    }

    .patient-label { font-size: 11px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; }
    .patient-name { font-size: 14px; font-weight: 600; color: var(--text); margin-top: 1px; }

    /* Card body */
    .card-body { padding: 24px; }

    .section-label {
        font-size: 11px; font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.7px;
        color: var(--text-muted);
        margin-bottom: 14px;
    }

    /* Contact card */
    .contact-card {
        border: 1px solid var(--border);
        border-radius: 10px;
        overflow: hidden;
        margin-bottom: 22px;
    }

    .contact-row {
        display: flex;
        align-items: center;
        gap: 14px;
        padding: 12px 16px;
        border-bottom: 1px solid #f1f5f9;
    }

    .contact-row:last-child { border-bottom: none; }

    .contact-icon {
        width: 32px; height: 32px;
        background: var(--red-light);
        border-radius: 7px;
        display: flex; align-items: center; justify-content: center;
        flex-shrink: 0;
    }

    .contact-icon svg {
        width: 14px; height: 14px;
        stroke: var(--red); fill: none;
        stroke-width: 1.8; stroke-linecap: round; stroke-linejoin: round;
    }

    .contact-field-label { font-size: 10px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; }
    .contact-field-value { font-size: 13px; font-weight: 500; color: var(--text); margin-top: 2px; }

    .no-contact {
        border: 1px dashed #fca5a5;
        border-radius: 10px;
        padding: 16px;
        text-align: center;
        font-size: 13px;
        color: #b91c1c;
        background: #fff5f5;
        margin-bottom: 22px;
    }

    /* Steps */
    .section-divider { height: 1px; background: var(--border); margin: 20px 0; }

    .step { display: flex; align-items: center; gap: 14px; margin-bottom: 14px; }

    .step-num {
        width: 28px; height: 28px;
        border-radius: 50%;
        background: var(--red);
        color: white;
        font-size: 12px; font-weight: 700;
        display: flex; align-items: center; justify-content: center;
        flex-shrink: 0;
    }

    .step-body { flex: 1; }

    .step-body input {
        width: 100%;
        padding: 11px 14px;
        border: 1.5px solid var(--border);
        border-radius: 8px;
        font-family: 'Inter', sans-serif;
        font-size: 14px;
        color: var(--text);
        background: #fafafa;
        outline: none;
        transition: border-color 0.15s, box-shadow 0.15s;
        letter-spacing: 3px;
    }

    .step-body input:focus {
        border-color: var(--red);
        box-shadow: 0 0 0 3px rgba(192,57,43,0.10);
        background: white;
    }

    .step-body input::placeholder { color: #bbb; letter-spacing: 0; }

    .btn {
        width: 100%;
        padding: 12px 16px;
        border: none;
        border-radius: 8px;
        font-family: 'Inter', sans-serif;
        font-size: 13px; font-weight: 600;
        cursor: pointer;
        display: flex; align-items: center; justify-content: center;
        gap: 8px;
        transition: background 0.15s, transform 0.12s, box-shadow 0.15s;
    }

    .btn svg {
        width: 15px; height: 15px;
        stroke: currentColor; fill: none;
        stroke-width: 2; stroke-linecap: round; stroke-linejoin: round;
    }

    .btn-danger {
        background: var(--red);
        color: white;
        box-shadow: 0 2px 8px rgba(192,57,43,0.22);
    }

    .btn-danger:hover { background: var(--red-dark); transform: translateY(-1px); box-shadow: 0 4px 14px rgba(192,57,43,0.28); }
    .btn-danger:active { transform: translateY(0); }

    .btn-danger-outline {
        background: white;
        color: var(--red);
        border: 1.5px solid var(--red);
    }

    .btn-danger-outline:hover { background: var(--red-light); transform: translateY(-1px); }
    .btn-danger-outline:active { transform: translateY(0); }

    .msg {
        display: none;
        margin-top: 16px;
        padding: 11px 14px;
        border-radius: 8px;
        font-size: 13px; font-weight: 500;
        border: 1px solid var(--border);
        background: #fafafa;
        color: var(--text-muted);
    }

    .msg.visible { display: flex; align-items: flex-start; gap: 8px; }

    .history-section { margin-top: 20px; }

    .history-title {
        font-size: 13px; font-weight: 600;
        color: var(--text);
        margin-bottom: 14px;
        display: flex; align-items: center; gap: 8px;
    }

    .history-title svg {
        width: 16px; height: 16px;
        stroke: var(--red); fill: none;
        stroke-width: 1.8; stroke-linecap: round; stroke-linejoin: round;
    }

    .card-footer {
        text-align: center;
        padding: 13px 24px;
        font-size: 11px;
        color: var(--text-muted);
        border-top: 1px solid var(--border);
        background: #fafbfc;
    }

    @media (max-width: 440px) {
        body { padding: 20px 12px; }
        .card-body { padding: 18px; }
        .patient-row, .card-header, .alert-bar { padding-left: 18px; padding-right: 18px; }
    }
</style>

</head>

<body>

<div class="wrapper">



<div class="card">

    <div class="card-header">
        <div class="header-left">
            <div class="header-icon">
                <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
            </div>
            <div>
                <div class="header-title">Emergency Access</div>
                <div class="header-sub">OTP-secured emergency records</div>
            </div>
        </div>
        <img src="uploads/logo_red.png" alt="MedVault" style="height:38px;width:auto;object-fit:contain;">

    </div>

    <div class="alert-bar">
        <svg viewBox="0 0 24 24"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
        For authorized emergency personnel only. OTP verification required.
    </div>

    <div class="patient-row">
        <div class="patient-avatar">
            <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>
        </div>
        <div>
            <div class="patient-label">Patient</div>
            <div class="patient-name"><?= htmlspecialchars($name) ?></div>
        </div>
    </div>

    <div class="card-body">

        <p class="section-label">Emergency Contact</p>

<?php
if(!empty($emergency_name) || !empty($emergency_email)){
?>
        <div class="contact-card">

            <div class="contact-row">
                <div class="contact-icon">
                    <svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>
                </div>
                <div>
                    <div class="contact-field-label">Name</div>
                    <div class="contact-field-value"><?= htmlspecialchars($emergency_name) ?></div>
                </div>
            </div>

            <div class="contact-row">
                <div class="contact-icon">
                    <svg viewBox="0 0 24 24"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
                </div>
                <div>
                    <div class="contact-field-label">Email</div>
                    <div class="contact-field-value"><?= htmlspecialchars($emergency_email) ?></div>
                </div>
            </div>

            <div class="contact-row">
                <div class="contact-icon">
                    <svg viewBox="0 0 24 24"><path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07A19.5 19.5 0 013.07 3.18 2 2 0 015.07 1h3a2 2 0 012 1.73 12.84 12.84 0 00.7 2.81 2 2 0 01-.45 2.11L9.16 8.82a16 16 0 006.02 6.02l1.17-1.16a2 2 0 012.11-.45 12.84 12.84 0 002.81.7A2 2 0 0122 16.92z"/></svg>
                </div>
                <div>
                    <div class="contact-field-label">Phone</div>
                    <div class="contact-field-value"><?= htmlspecialchars($emergency_phone) ?></div>
                </div>
            </div>

        </div>
<?php
}
else
{
echo "<div class='no-contact'>No emergency contact registered for this patient.</div>";
}
?>

        <div class="section-divider"></div>
        <p class="section-label">Verification Steps</p>

        <div class="step">
            <div class="step-num">1</div>
            <div class="step-body">
                <button class="btn btn-danger" onclick="sendOTP()">
                    <svg viewBox="0 0 24 24"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
                    Send OTP to Emergency Email
                </button>
            </div>
        </div>

        <div class="step">
            <div class="step-num">2</div>
            <div class="step-body">
                <input type="text" id="otp" placeholder="Enter OTP" maxlength="6" autocomplete="off">
            </div>
        </div>

        <div class="step">
            <div class="step-num">3</div>
            <div class="step-body">
                <button class="btn btn-danger-outline" onclick="verifyOTP()">
                    <svg viewBox="0 0 24 24"><polyline points="20 6 9 17 4 12"/></svg>
                    Verify OTP &amp; View Records
                </button>
            </div>
        </div>

        <div class="msg" id="msg">
            <svg width="15" height="15" viewBox="0 0 24 24" style="stroke:currentColor;fill:none;stroke-width:2;flex-shrink:0;margin-top:1px"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            <span id="msg-text"></span>
        </div>

        <div id="history" style="display:none;" class="history-section">
            <div class="section-divider"></div>
            <div class="history-title">
                <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
                Medical History
            </div>
            <div id="historyData"></div>
        </div>

    </div>

    <div class="card-footer">MedVault &nbsp;·&nbsp; Confidential — Authorized Access Only</div>

</div>

</div>

<script>

const publicId = "<?= htmlspecialchars($public_id) ?>";

function showMsg(text) {
    const el = document.getElementById("msg");
    document.getElementById("msg-text").innerText = text;
    el.classList.add("visible");
}

function sendOTP(){

fetch("send_emergency_otp.php",{
method:"POST",
headers:{"Content-Type":"application/x-www-form-urlencoded"},
body:"public_id="+encodeURIComponent(publicId)
})

.then(res=>res.json())

.then(data=>{ showMsg(data.message); })

.catch(()=>{ showMsg("Failed to send OTP"); });

}

function verifyOTP(){

let otp=document.getElementById("otp").value.trim();

fetch("verify_emergency_otp.php",{
method:"POST",
headers:{"Content-Type":"application/x-www-form-urlencoded"},
body:"public_id="+encodeURIComponent(publicId)+"&otp="+encodeURIComponent(otp)
})

.then(res=>res.json())

.then(data=>{

    showMsg(data.message);

    if(data.success){
    loadHistory();
    }

})

.catch(()=>{ showMsg("OTP verification failed"); });

}

function loadHistory(){

fetch("doctor_medical_history.php?id="+encodeURIComponent(publicId))

.then(res=>res.text())

.then(html=>{

document.getElementById("history").style.display="block";
document.getElementById("historyData").innerHTML=html;

});

}

</script>

</body>
</html>