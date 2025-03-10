-- Insert Users
INSERT INTO users (username, email, password, role, display_image_url, enabled, account_non_expired, account_non_locked, credentials_non_expired) VALUES
                                                                                                                                                      ('admin_user', 'admin@example.com', 'hashed_password_1', 'ADMIN', 'https://example.com/avatar1.png', true, true, true, true),
                                                                                                                                                      ('john_doe', 'john@example.com', 'hashed_password_2', 'USER', 'https://example.com/avatar2.png', true, true, true, true),
                                                                                                                                                      ('jane_smith', 'jane@example.com', 'hashed_password_3', 'USER', 'https://example.com/avatar3.png', true, true, true, true);

-- Insert Projects
INSERT INTO project (name, description, status, display_image_url, owner_id) VALUES
                                                                                 ('Project Alpha', 'First test project', 'ACTIVE', 'https://example.com/project1.png', 1),
                                                                                 ('Project Beta', 'Second test project', 'ACTIVE', 'https://example.com/project2.png', 2);

-- Insert Issues
INSERT INTO issue (title, description, status, created_at, priority_status, project_id, reported_by_id, assigned_to_id) VALUES
                                                                                                                ('Bug Fix A', 'Fix a critical bug in system', 'OPEN', '2023-03-12', 'HIGH', 1, 1, 2),
                                                                                                                ('UI Enhancement', 'Improve UI responsiveness', 'IN_PROGRESS', '2024-03-12', 'MEDIUM', 1, 2, 3);

-- Insert Tasks
INSERT INTO task (description, due_date,created_at, priority, status, project_id) VALUES
                                                                           ('Develop login feature', '2025-03-01', '2025-02-01','HIGH', 'OPEN', 1),
                                                                           ('Create project dashboard', '2025-03-05', '2025-02-01','HIGH', 'OPEN', 1);

-- Assign Users to Tasks
INSERT INTO task_user (task_id, user_id) VALUES
                                             (1, 2),
                                             (2, 3);

-- Assign Users to Projects
INSERT INTO project_user (project_id, user_id) VALUES
                                                   (1, 2),
                                                   (1, 3),
                                                   (2, 3);

-- Insert Comments
INSERT INTO comment (content, created_at,task_id, issue_id, user_id) VALUES
                                                                                      ('This task is almost done.', '2025-02-15', 1, NULL, 2),
                                                                                      ('We need more details.', '2025-02-16',  NULL, 1, 3);

-- Insert Attachments
INSERT INTO attachment (file_name, file_path, file_type, uploaded_at, task_id, issue_id,author_id) VALUES
                                                                                             ('design_mockup.png', '/uploads/design_mockup.png', 'image/png', '2025-02-14', 2, NULL, 3),
                                                                                             ('error_log.txt', '/uploads/error_log.txt', 'text/plain', '2025-02-15', NULL, 1,2);

-- Insert Notifications
INSERT INTO notification (type, message, is_read, created_at) VALUES
                                                                  ('CREATION', 'New task created successfully.', false, '2025-02-15'),
                                                                  ('UPDATE', 'Task "Develop login feature" was updated.', false, '2025-02-16'),
                                                                  ('ASSIGNMENT', 'Issue "Bug Fix A" assigned to John Doe.', true, '2025-02-16'),
                                                                  ('COMPLETION', 'Project Alpha has been completed.', false, '2025-02-17');