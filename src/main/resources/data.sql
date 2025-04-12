-- Insert Users
INSERT INTO users (username, email, password, role, display_image_url, enabled, account_non_expired, account_non_locked, credentials_non_expired) VALUES
                                                                                                                                                      ('admin_user', 'admin@example.com', 'hashed_password_1', 'ADMIN', 'https://example.com/avatar1.png', true, true, true, true),
                                                                                                                                                      ('john_doe', 'john@example.com', 'hashed_password_2', 'USER', 'https://example.com/avatar2.png', true, true, true, true),
                                                                                                                                                      ('jane_smith', 'jane@example.com', 'hashed_password_3', 'USER', 'https://example.com/avatar3.png', true, true, true, true),
                                                                                                                                                      ('emma_watson', 'emma@example.com', 'hashed_password_4', 'USER', 'https://example.com/avatar4.png', true, true, true, true),
                                                                                                                                                      ('liam_clark', 'liam@example.com', 'hashed_password_5', 'USER', 'https://example.com/avatar5.png', true, true, true, true);

-- Insert Projects
INSERT INTO project (name, description, status, display_image_url, owner_id) VALUES
                                                                                 ('Project Alpha', 'First test project', 'ACTIVE', 'https://example.com/project1.png', 1),
                                                                                 ('Project Beta', 'Second test project', 'ACTIVE', 'https://example.com/project2.png', 2),
                                                                                 ('InvoicePilot', 'Invoicing automation tool for freelancers', 'ACTIVE', 'https://example.com/project3.png', 3);

-- Insert Issues
INSERT INTO issue (title, description, status, priority_status, project_id, reported_by_id, assigned_to_id) VALUES
                                                                                                                ('Bug Fix A', 'Fix a critical bug in system', 'OPEN', 'HIGH', 1, 1, 2),
                                                                                                                ('UI Enhancement', 'Improve UI responsiveness', 'IN_PROGRESS', 'MEDIUM', 1, 2, 3),
                                                                                                                ('PDF Export Error', 'Exported invoices missing header section', 'OPEN', 'HIGH', 3, 3, 4),
                                                                                                                ('Data Sync Delay', 'Real-time sync takes 10+ seconds to update', 'OPEN', 'LOW', 2, 2, 5);

-- Insert Tasks
INSERT INTO task (description, due_date, created_at, priority, status, project_id) VALUES
                                                                                       ('Develop login feature', '2025-03-01','2025-02-01', 'HIGH', 'OPEN', 1),
                                                                                       ('Create project dashboard', '2025-03-05','2025-02-05', 'HIGH', 'OPEN', 1),
                                                                                       ('Build invoice preview modal', '2025-03-10','2025-02-10', 'MEDIUM', 'IN_PROGRESS', 3),
                                                                                       ('Implement real-time updates', '2025-03-15','2025-02-15', 'HIGH', 'OPEN', 2),
                                                                                       ('Fix footer in exported PDFs', '2025-03-12','2025-02-12', 'MEDIUM', 'OPEN', 3);

-- Assign Users to Tasks
INSERT INTO task_user (task_id, user_id) VALUES
                                             (1, 2),
                                             (2, 3),
                                             (3, 4),
                                             (4, 5),
                                             (5, 4);

-- Assign Collaborators to Projects
INSERT INTO project_collaborators (project_id, collaborator_id) VALUES
                                                   (1, 2),
                                                   (1, 3),
                                                   (2, 3),
                                                   (2, 5),
                                                   (3, 4),
                                                   (3, 5);

-- Insert Comments
INSERT INTO comment (content, created_at, updated_at, task_id, issue_id, user_id, is_edited) VALUES
                                                                                                             ('This task is almost done.', '2025-02-15', NULL, 1, NULL, 2, false),
                                                                                                             ('We need more details.', '2025-02-16', '2025-02-17', NULL, 1, 3, true),
                                                                                                             ('Working on invoice modal, will push PR tomorrow.', '2025-02-18', NULL, 3, NULL, 4, false),
                                                                                                             ('Investigating why export fails on Safari.', '2025-02-19', NULL, NULL, 3, 4, false),
                                                                                                             ('Real-time sync improved after WebSocket refactor.', '2025-02-20', NULL, 4, NULL, 5, false);

-- Insert Attachments
INSERT INTO attachment (file_name, file_path, file_type, uploaded_at, task_id, issue_id, author_id) VALUES
                                                                                                        ('design_mockup.png', '/uploads/design_mockup.png', 'image/png', '2025-02-14', 2, NULL, 3),
                                                                                                        ('error_log.txt', '/uploads/error_log.txt', 'text/plain', '2025-02-15', NULL, 1, 2),
                                                                                                        ('invoice_modal_wireframe.pdf', '/uploads/invoice_modal_wireframe.pdf', 'application/pdf', '2025-02-18', 3, NULL, 4),
                                                                                                        ('sync_test_report.xlsx', '/uploads/sync_test_report.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '2025-02-20', 4, NULL, 5);

-- Insert Notifications
INSERT INTO notification (type, message, is_read, created_at) VALUES
                                                                  ('UPDATE', 'Your task has been updated.', false, '2023-10-01'),
                                                                  ('UPDATE', 'Deadline is approaching for your project.', false, '2023-10-02'),
                                                                  ('ASSIGNMENT', 'Failed to assign the task to the user.', true, '2023-10-03'),
                                                                  ('CREATION', 'New comment added to your issue.', false, '2023-10-04'),
                                                                  ('COMPLETION', 'Your issue has been resolved.', true, '2023-10-05');