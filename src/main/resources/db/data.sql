INSERT INTO news (id, title, summary, content, image_url, published_at, author, status)
VALUES
    (1,
     'Ecuador Comparte lanza el Programa EDIFICA 2026',
     'Nuevo programa de desarrollo personal diseñado para transformar vidas en las 24 provincias del Ecuador.',
     'Ecuador Comparte se complace en anunciar el lanzamiento oficial del Programa EDIFICA 2026, una iniciativa de desarrollo personal que busca llegar a todas las provincias del país. El programa incluye talleres presenciales, sesiones de coaching y acompañamiento continuo.',
     '',
     '2026-01-15 09:00:00',
     'Equipo Ecuador Comparte',
     'PUBLISHED'),

    (2,
     'Conferencia motivacional en Quito reunió a más de 500 personas',
     'El evento contó con la participación de líderes empresariales y emprendedores de todo el país.',
     'La conferencia motivacional organizada por Ecuador Comparte en la ciudad de Quito superó todas las expectativas, reuniendo a más de 500 asistentes entre empresarios, emprendedores y familias que buscan mejorar su calidad de vida.',
     '',
     '2026-02-10 14:00:00',
     'Equipo Ecuador Comparte',
     'PUBLISHED'),

    (3,
     'Nuevas alianzas estratégicas con empresas de Guayaquil',
     'Ecuador Comparte firma convenios con 15 nuevas empresas para ampliar su red de apoyo empresarial.',
     'Con el objetivo de fortalecer nuestra red de aliados, Ecuador Comparte firmó convenios estratégicos con 15 empresas de la ciudad de Guayaquil, ampliando así el alcance de nuestros servicios de acompañamiento empresarial.',
     '',
     '2026-03-05 10:30:00',
     'Equipo Ecuador Comparte',
     'DRAFT')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO testimonials (id, name, photo_url, instagram_url, facebook_url)
VALUES
    (1, 'María González', '', 'https://instagram.com', ''),
    (2, 'Carlos Mendoza', '', '', 'https://facebook.com'),
    (3, 'Ana Rodríguez', '', 'https://instagram.com', 'https://facebook.com'),
    (4, 'Luis Paredes',  '', '', '')
    ON CONFLICT (id) DO NOTHING;