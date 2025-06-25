-- Initial data for LOR Management System
USE lor_management;

-- Sample student data for admin verification
INSERT INTO admin_students (name, registration_number, examination_number, course) VALUES
('Ankit Lakra', '22SXC051718', '22VBCA051718', 'Computer Science'),
('Anmol Kujur', '22SXC051719', '22VBCA051719', 'Computer Science');

-- Sample professor data for admin verification
INSERT INTO admin_professors (name, user_id, department) VALUES
('A', 'PROF001', 'Computer Science'),
('B', 'PROF002', 'Computer Science'),
('C', 'PROF026', 'Electronics and Communication');