-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Feb 05, 2026 at 08:04 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `medvault`
--

-- --------------------------------------------------------

--
-- Table structure for table `medical_history`
--

CREATE TABLE `medical_history` (
  `id` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `file_name` varchar(100) NOT NULL,
  `date_time` datetime NOT NULL,
  `notes` varchar(255) NOT NULL,
  `status` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `medical_history`
--

INSERT INTO `medical_history` (`id`, `uid`, `title`, `file_name`, `date_time`, `notes`, `status`) VALUES
(1, 1, 'test title', 'test.pdf', '2026-02-05 13:49:19', 'test notes', ''),
(2, 1, 'test 2 title', 'test2.pdf', '2026-02-05 13:49:19', 'test 2 notes', ''),
(3, 1, 'abin', 'uploads/medical/1770304341_6984b355b2f2b.png', '2026-02-05 13:49:19', '', 'active'),
(4, 1, 't', 'uploads/medical/1770304347_6984b35b6b31b.pdf', '2026-02-05 13:49:19', 'ttttt', 'active'),
(5, 1, 'yyyyyy', 'uploads/medical/1770310652_6984cbfc69eb3.pdf', '2026-02-05 13:49:19', 'gggghhhh', 'active');

-- --------------------------------------------------------

--
-- Table structure for table `medicine_reminders`
--

CREATE TABLE `medicine_reminders` (
  `id` int(11) NOT NULL,
  `uid` int(11) DEFAULT NULL,
  `medicine` varchar(100) DEFAULT NULL,
  `dosage` varchar(100) DEFAULT NULL,
  `time` varchar(10) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `days` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `medicine_reminders`
--

INSERT INTO `medicine_reminders` (`id`, `uid`, `medicine`, `dosage`, `time`, `start_date`, `days`, `created_at`) VALUES
(1, 1, 'test', '1 tab', '22:03', '2026-02-05', 2, '2026-02-05 16:31:55');

-- --------------------------------------------------------

--
-- Table structure for table `reminders`
--

CREATE TABLE `reminders` (
  `id` int(11) NOT NULL,
  `uid` int(11) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `time` varchar(10) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `days` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reminders`
--

INSERT INTO `reminders` (`id`, `uid`, `title`, `description`, `time`, `start_date`, `days`, `created_at`) VALUES
(1, 1, 'test title', 'test descp', '23:22', '2026-02-05', 2, '2026-02-05 17:50:16'),
(2, 1, 'test', 'test', '23:27', '2026-02-04', 3, '2026-02-05 17:56:01'),
(3, 1, 'yyyy', 'yyyy', '23:30', '2026-02-04', 3, '2026-02-05 17:57:23');

-- --------------------------------------------------------

--
-- Table structure for table `user_details`
--

CREATE TABLE `user_details` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `age` int(5) NOT NULL,
  `blood_group` varchar(5) NOT NULL,
  `allergies` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `email` varchar(20) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `password` varchar(20) NOT NULL,
  `status` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_details`
--

INSERT INTO `user_details` (`id`, `name`, `age`, `blood_group`, `allergies`, `address`, `email`, `phone`, `password`, `status`) VALUES
(1, 'Abin', 30, 'A+', 'test', 'tes', 'abin@gmail.com', '7012656981', 'qwerty12345', '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `medical_history`
--
ALTER TABLE `medical_history`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `medicine_reminders`
--
ALTER TABLE `medicine_reminders`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `reminders`
--
ALTER TABLE `reminders`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_details`
--
ALTER TABLE `user_details`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `medical_history`
--
ALTER TABLE `medical_history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `medicine_reminders`
--
ALTER TABLE `medicine_reminders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `reminders`
--
ALTER TABLE `reminders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `user_details`
--
ALTER TABLE `user_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
