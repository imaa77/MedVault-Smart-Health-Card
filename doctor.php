<?php
include("connection.php");

if (!isset($_GET['id'])) die("Invalid QR");
$public_id = trim($_GET['id']);

$sql = "SELECT id, name FROM user_details WHERE public_id=?";
$stmt = $con->prepare($sql);
$stmt->bind_param("s", $public_id);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows == 0) die("User not found");

$user = $res->fetch_assoc();
$name = $user['name'];
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Doctor Access – MedVault</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="MedVault secure doctor access portal">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --blue: #0077B6;
            --blue-dark: #005f8e;
            --blue-light: #e8f4fb;
            --blue-mid: #cce6f5;
            --border: #e2e8f0;
            --text: #1a202c;
            --text-muted: #64748b;
            --bg: #f0f4f8;
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
            background: var(--blue-light);
            border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
        }

        .header-icon svg {
            width: 20px; height: 20px;
            stroke: var(--blue); fill: none;
            stroke-width: 1.7; stroke-linecap: round; stroke-linejoin: round;
        }

        .header-title { font-size: 15px; font-weight: 600; letter-spacing: -0.2px; }
        .header-sub { font-size: 12px; color: var(--text-muted); margin-top: 2px; }

        .badge {
            font-size: 11px; font-weight: 500;
            color: var(--blue);
            background: var(--blue-light);
            border: 1px solid var(--blue-mid);
            padding: 3px 10px;
            border-radius: 20px;
            white-space: nowrap;
        }

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
            background: var(--blue-light);
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            flex-shrink: 0;
        }

        .patient-avatar svg {
            width: 18px; height: 18px;
            stroke: var(--blue); fill: none;
            stroke-width: 1.7; stroke-linecap: round; stroke-linejoin: round;
        }

        .patient-label { font-size: 11px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; }
        .patient-name { font-size: 14px; font-weight: 600; color: var(--text); margin-top: 1px; }

        .card-body { padding: 24px; }

        .section-label {
            font-size: 11px; font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.7px;
            color: var(--text-muted);
            margin-bottom: 16px;
        }

        .step { display: flex; align-items: center; gap: 14px; margin-bottom: 14px; }

        .step-num {
            width: 28px; height: 28px;
            border-radius: 50%;
            background: var(--blue);
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
            border-color: var(--blue);
            box-shadow: 0 0 0 3px rgba(0,119,182,0.12);
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

        .btn-primary {
            background: var(--blue);
            color: white;
            box-shadow: 0 2px 8px rgba(0,119,182,0.22);
        }

        .btn-primary:hover { background: var(--blue-dark); transform: translateY(-1px); box-shadow: 0 4px 14px rgba(0,119,182,0.28); }
        .btn-primary:active { transform: translateY(0); }

        .btn-outline {
            background: white;
            color: var(--blue);
            border: 1.5px solid var(--blue);
        }

        .btn-outline:hover { background: var(--blue-light); transform: translateY(-1px); }
        .btn-outline:active { transform: translateY(0); }

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

        .section-divider { height: 1px; background: var(--border); margin: 20px 0; }

        .history-title {
            font-size: 13px; font-weight: 600;
            color: var(--text);
            margin-bottom: 14px;
            display: flex; align-items: center; gap: 8px;
        }

        .history-title svg {
            width: 16px; height: 16px;
            stroke: var(--blue); fill: none;
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
            .patient-row, .card-header { padding-left: 18px; padding-right: 18px; }
        }
    </style>
</head>
<body>

<div class="wrapper">



    <div class="card">

        <div class="card-header">
            <div class="header-left">
                <div class="header-icon">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="M19 3H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2V5a2 2 0 00-2-2zm-7 3a4 4 0 110 8 4 4 0 010-8zm6 13H6v-.5C6 17 9 15 12 15s6 2 6 3.5V19z"/></svg>
                </div>
                <div>
                    <div class="header-title">Doctor Access</div>
                    <div class="header-sub">OTP-secured medical records</div>
                </div>
            </div>
            <img src="uploads/logo_blue.png" alt="MedVault" style="height:38px;width:auto;object-fit:contain;">

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

            <p class="section-label">Verification Steps</p>

            <div class="step">
                <div class="step-num">1</div>
                <div class="step-body">
                    <button class="btn btn-primary" onclick="sendOTP()">
                        <svg viewBox="0 0 24 24"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
                        Send OTP to Patient Email
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
                    <button class="btn btn-outline" onclick="verifyOTP()">
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
                    <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
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
    fetch("send_doctor_otp.php", {
        method:"POST",
        headers:{"Content-Type":"application/x-www-form-urlencoded"},
        body:"public_id=" + encodeURIComponent(publicId)
    })
    .then(res=>res.json())
    .then(data=>{ showMsg(data.message); })
    .catch(err=>{ showMsg("Error sending OTP"); console.log(err); });
}

function verifyOTP(){
    let otp = document.getElementById("otp").value.trim(); // IMPORTANT

    fetch("verify_doctor_otp.php", {
        method:"POST",
        headers:{"Content-Type":"application/x-www-form-urlencoded"},
        body:"public_id=" + encodeURIComponent(publicId) + "&otp=" + encodeURIComponent(otp)
    })
    .then(res=>res.json())
    .then(data=>{
        showMsg(data.message);
        if(data.success){ loadHistory(); }
    })
    .catch(err=>{ showMsg("Error verifying OTP"); console.log(err); });
}

function loadHistory(){
    fetch("doctor_medical_history.php?id=" + encodeURIComponent(publicId))
    .then(res=>res.text())
    .then(html=>{
        document.getElementById("history").style.display="block";
        document.getElementById("historyData").innerHTML = html;
    });
}
</script>

</body>
</html>
