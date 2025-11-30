-- Initial Data for trvly.co

-- Insert Roles
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrador con acceso total al sistema'),
('SUPERVISOR', 'Supervisor con permisos de gestión y eliminación'),
('ADVISOR', 'Asesor con permisos de lectura y escritura')
ON CONFLICT (name) DO NOTHING;

-- Insert Permissions
INSERT INTO permissions (name, description) VALUES
('user_management', 'Gestión completa de usuarios'),
('package_management', 'Gestión completa de paquetes turísticos'),
('client_management_r', 'Lectura de clientes'),
('client_management_w', 'Escritura de clientes'),
('client_management_d', 'Eliminación de clientes'),
('reservation_management_r', 'Lectura de reservas'),
('reservation_management_w', 'Escritura de reservas'),
('reservation_management_d', 'Eliminación de reservas')
ON CONFLICT (name) DO NOTHING;

-- Assign permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Assign permissions to SUPERVISOR role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'SUPERVISOR' 
  AND p.name IN (
    'client_management_r', 'client_management_w', 'client_management_d',
    'reservation_management_r', 'reservation_management_w', 'reservation_management_d'
  )
ON CONFLICT DO NOTHING;

-- Assign permissions to ADVISOR role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADVISOR' 
  AND p.name IN (
    'client_management_r', 'client_management_w',
    'reservation_management_r', 'reservation_management_w'
  )
ON CONFLICT DO NOTHING;

-- Create default admin user (password: admin123 - should be changed in production)
-- Password is bcrypt hash of "admin123"
INSERT INTO users (username, password, email, first_name, last_name, enabled)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJwqO', 'admin@trvly.co', 'Admin', 'User', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Assign ADMIN role to default admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

