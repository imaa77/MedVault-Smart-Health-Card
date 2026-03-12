<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include("connection.php");

if (!isset($_GET['id'])) {
    die("Invalid QR");
}

$public_id = $_GET['id'];

// Fetch basic user details
$sql = "SELECT name, dob, blood_group, allergies, address, email, phone, public_id 
        FROM user_details 
        WHERE public_id = ?";

$stmt = $con->prepare($sql);
$stmt->bind_param("s", $public_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows == 0) {
    die("User not found");
}

$user = $result->fetch_assoc();
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>MedVault – Patient Record</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="MedVault secure patient information card">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --blue: #0077B6;
            --blue-dark: #005f8e;
            --blue-light: #e8f4fb;
            --blue-mid: #cce6f5;
            --red: #C0392B;
            --red-dark: #a02b20;
            --red-light: #fdf2f1;
            --red-mid: #f5c6c0;
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

        .wrapper {
            width: 100%;
            max-width: 500px;
        }

        /* Logo banner inside card */
        .logo-banner {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px 24px 16px;
            border-bottom: 1px solid var(--border);
            background: var(--white);
        }

        .logo-banner img {
            height: 90px;
            width: auto;
            object-fit: contain;
        }

        /* Card */
        .card {
            background: var(--white);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            border: 1px solid var(--border);
            overflow: hidden;
        }

        .card-header {
            padding: 20px 24px 16px;
            border-bottom: 1px solid var(--border);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .card-header h2 {
            font-size: 15px;
            font-weight: 600;
            color: var(--text);
            letter-spacing: -0.2px;
        }

        .card-header .badge {
            font-size: 11px;
            font-weight: 500;
            color: var(--blue);
            background: var(--blue-light);
            border: 1px solid var(--blue-mid);
            padding: 3px 10px;
            border-radius: 20px;
        }

        .card-body { padding: 20px 24px; }

        /* Info rows */
        .info-item {
            display: flex;
            align-items: center;
            gap: 14px;
            padding: 12px 0;
            border-bottom: 1px solid #f1f5f9;
        }

        .info-item:last-of-type { border-bottom: none; }

        .info-icon-wrap {
            width: 36px;
            height: 36px;
            border-radius: 8px;
            background: var(--blue-light);
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }

        .info-icon-wrap svg {
            width: 17px;
            height: 17px;
            stroke: var(--blue);
            fill: none;
            stroke-width: 1.7;
            stroke-linecap: round;
            stroke-linejoin: round;
        }

        .info-label {
            font-size: 10.5px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.6px;
            color: var(--text-muted);
            margin-bottom: 2px;
        }

        .info-value {
            font-size: 14px;
            font-weight: 500;
            color: var(--text);
        }

        /* Divider */
        .divider {
            height: 1px;
            background: var(--border);
            margin: 20px 24px;
        }

        /* Access section */
        .access-header {
            padding: 0 24px 14px;
            font-size: 11px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.7px;
            color: var(--text-muted);
        }

        .action-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
            padding: 0 24px 24px;
        }

        .action-btn {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            gap: 6px;
            text-decoration: none;
            padding: 16px;
            border-radius: var(--radius);
            border: 1px solid transparent;
            transition: transform 0.15s, box-shadow 0.15s, background 0.15s;
        }

        .action-btn:hover {
            transform: translateY(-1px);
            box-shadow: 0 6px 20px rgba(0,0,0,0.10);
        }

        .action-btn:active { transform: translateY(0); }

        .action-btn .btn-icon {
            width: 32px;
            height: 32px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 2px;
        }

        .action-btn .btn-icon svg {
            width: 17px;
            height: 17px;
            stroke-width: 1.8;
            stroke-linecap: round;
            stroke-linejoin: round;
            fill: none;
        }

        .action-btn .btn-label {
            font-size: 13px;
            font-weight: 600;
        }

        .action-btn .btn-sub {
            font-size: 11px;
            opacity: 0.75;
        }

        .btn-doctor {
            background: var(--blue);
            color: white;
            box-shadow: 0 4px 14px rgba(0, 119, 182, 0.28);
        }

        .btn-doctor .btn-icon { background: rgba(255,255,255,0.18); }
        .btn-doctor .btn-icon svg { stroke: white; }

        .btn-doctor:hover { background: var(--blue-dark); }

        .btn-emergency {
            background: var(--red);
            color: white;
            box-shadow: 0 4px 14px rgba(192, 57, 43, 0.28);
        }

        .btn-emergency .btn-icon { background: rgba(255,255,255,0.18); }
        .btn-emergency .btn-icon svg { stroke: white; }

        .btn-emergency:hover { background: var(--red-dark); }

        /* Footer */
        .card-footer {
            text-align: center;
            padding: 14px 24px;
            font-size: 11px;
            color: var(--text-muted);
            border-top: 1px solid var(--border);
            background: #fafbfc;
            letter-spacing: 0.2px;
        }

        @media (max-width: 440px) {
            body { padding: 24px 12px; }
            .action-grid { grid-template-columns: 1fr; }
            .card-body { padding: 16px 18px; }
            .card-header, .access-header, .action-grid { padding-left: 18px; padding-right: 18px; }
            .divider { margin: 16px 18px; }
        }
    </style>
</head>
<body>

<div class="wrapper">


    <div class="card">

        <div class="logo-banner">
            <img src="uploads/logo_blue.png" alt="MedVault">
        </div>

        <div class="card-header">
            <h2>Patient Information</h2>
            <span class="badge">Medical Record</span>
        </div>

        <div class="card-body">

            <div class="info-item">
                <div class="info-icon-wrap">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>
                </div>
                <div>
                    <div class="info-label">Full Name</div>
                    <div class="info-value"><?= htmlspecialchars($user['name']) ?></div>
                </div>
            </div>

            <div class="info-item">
                <div class="info-icon-wrap">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>
                </div>
                <div>
                    <div class="info-label">Date of Birth</div>
                    <div class="info-value"><?= htmlspecialchars($user['dob']) ?></div>
                </div>
            </div>

            <div class="info-item">
                <div class="info-icon-wrap">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="M12 21C12 21 4 13.5 4 8.5A5 5 0 0112 4.9 5 5 0 0120 8.5C20 13.5 12 21 12 21z"/></svg>
                </div>
                <div>
                    <div class="info-label">Blood Group</div>
                    <div class="info-value"><?= htmlspecialchars($user['blood_group']) ?></div>
                </div>
            </div>

            <div class="info-item">
                <div class="info-icon-wrap">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                </div>
                <div>
                    <div class="info-label">Known Allergies</div>
                    <div class="info-value"><?= htmlspecialchars($user['allergies']) ?></div>
                </div>
            </div>

            <div class="info-item">
                <div class="info-icon-wrap">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07A19.5 19.5 0 013.07 3.18 2 2 0 015.07 1h3a2 2 0 012 1.73 12.84 12.84 0 00.7 2.81 2 2 0 01-.45 2.11L9.16 8.82a16 16 0 006.02 6.02l1.17-1.16a2 2 0 012.11-.45 12.84 12.84 0 002.81.7A2 2 0 0122 16.92z"/></svg>
                </div>
                <div>
                    <div class="info-label">Phone</div>
                    <div class="info-value"><?= htmlspecialchars($user['phone']) ?></div>
                </div>
            </div>

        </div>

        <div class="divider"></div>

        <p class="access-header">Secure Access</p>

        <div class="action-grid">
            <a href="doctor.php?id=<?= urlencode($public_id) ?>" class="action-btn btn-doctor">
                <div class="btn-icon">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="M19 3H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2V5a2 2 0 00-2-2zm-7 3a4 4 0 110 8 4 4 0 010-8zm6 13H6v-.5C6 17 9 15 12 15s6 2 6 3.5V19z"/></svg>
                </div>
                <div class="btn-label">Doctor Access</div>
                <div class="btn-sub">OTP Verified</div>
            </a>
            <a href="emergency.php?id=<?= urlencode($public_id) ?>" class="action-btn btn-emergency">
                <div class="btn-icon">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
                </div>
                <div class="btn-label">Emergency</div>
                <div class="btn-sub">Contact Access</div>
            </a>
        </div>

        <div class="card-footer">Powered by MedVault &nbsp;·&nbsp; Confidential Medical Record</div>

    </div>

</div>

</body>
</html>