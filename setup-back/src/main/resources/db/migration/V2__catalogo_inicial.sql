-- V2: catálogo inicial (pré-condição do sistema). Preços aproximados, em R$.

-- Placas-mãe
INSERT INTO componente (categoria, marca, modelo, descricao, preco, socket, tipo_memoria, formato, slots_ram, ram_max_mhz, slots_m2) VALUES
('placa', 'ASUS',     'Placa-mãe B650M',  'AM5 · DDR5 · mATX · 2 M.2',     899.00,  'AM5',     'DDR5', 'mATX', 4, 6400, 2),
('placa', 'Gigabyte', 'Placa-mãe B550M',  'AM4 · DDR4 · mATX · 1 M.2',     629.00,  'AM4',     'DDR4', 'mATX', 4, 3600, 1),
('placa', 'MSI',      'Placa-mãe B760M',  'LGA1700 · DDR4 · mATX · 2 M.2', 739.00,  'LGA1700', 'DDR4', 'mATX', 4, 3200, 2),
('placa', 'ASUS',     'Placa-mãe Z790',   'LGA1700 · DDR5 · ATX · 3 M.2',  1649.00, 'LGA1700', 'DDR5', 'ATX',  4, 7200, 3),
('placa', 'ASRock',   'Placa-mãe X670E',  'AM5 · DDR5 · ATX · 4 M.2',      1899.00, 'AM5',     'DDR5', 'ATX',  4, 7600, 4),
('placa', 'Gigabyte', 'Placa-mãe A620I',  'AM5 · DDR5 · ITX · 1 M.2',      949.00,  'AM5',     'DDR5', 'ITX',  2, 6000, 1);

-- Processadores
INSERT INTO componente (categoria, marca, modelo, descricao, preco, socket, tdp_w) VALUES
('cpu', 'AMD',   'Ryzen 5 7600',    'AM5 · 6 núcleos · 65 W',      1299.00, 'AM5',     65),
('cpu', 'AMD',   'Ryzen 7 7800X3D', 'AM5 · 8 núcleos · 120 W',     2599.00, 'AM5',     120),
('cpu', 'AMD',   'Ryzen 5 5600',    'AM4 · 6 núcleos · 65 W',      699.00,  'AM4',     65),
('cpu', 'AMD',   'Ryzen 7 5700X',   'AM4 · 8 núcleos · 65 W',      1049.00, 'AM4',     65),
('cpu', 'Intel', 'Core i5-12400F',  'LGA1700 · 6 núcleos · 65 W',  749.00,  'LGA1700', 65),
('cpu', 'Intel', 'Core i5-14600K',  'LGA1700 · 14 núcleos · 125 W', 1799.00, 'LGA1700', 125);

-- Memórias RAM (preço e consumo por pente)
INSERT INTO componente (categoria, marca, modelo, descricao, preco, tipo, frequencia_mhz, tdp_w) VALUES
('ram', 'Kingston', '8 GB DDR4 3200 MHz',  'DDR4 · preço por pente', 125.00, 'DDR4', 3200, 3),
('ram', 'Corsair',  '16 GB DDR4 3600 MHz', 'DDR4 · preço por pente', 239.00, 'DDR4', 3600, 3),
('ram', 'Corsair',  '8 GB DDR5 5200 MHz',  'DDR5 · preço por pente', 175.00, 'DDR5', 5200, 3),
('ram', 'Corsair',  '16 GB DDR5 6000 MHz', 'DDR5 · preço por pente', 340.00, 'DDR5', 6000, 3);

-- Placas de vídeo
INSERT INTO componente (categoria, marca, modelo, descricao, preco, tdp_w, comprimento_mm) VALUES
('gpu', 'NVIDIA', 'GeForce RTX 4060',       '8 GB · 115 W · 240 mm',  1999.00, 115, 240),
('gpu', 'AMD',    'Radeon RX 7600',         '8 GB · 165 W · 240 mm',  1599.00, 165, 240),
('gpu', 'NVIDIA', 'GeForce RTX 4070 SUPER', '12 GB · 220 W · 300 mm', 4299.00, 220, 300),
('gpu', 'AMD',    'Radeon RX 7800 XT',      '16 GB · 263 W · 320 mm', 3699.00, 263, 320);

-- Armazenamento
INSERT INTO componente (categoria, marca, modelo, descricao, preco, tipo, tdp_w) VALUES
('armazenamento', 'WD',       'SSD NVMe 1 TB',   'M.2 · até 5000 MB/s', 449.00, 'M.2',  8),
('armazenamento', 'Samsung',  'SSD NVMe 2 TB',   'M.2 · até 7000 MB/s', 899.00, 'M.2',  8),
('armazenamento', 'Kingston', 'SSD SATA 480 GB', 'SATA · 2,5"',         229.00, 'SATA', 4),
('armazenamento', 'Seagate',  'HD 1 TB',         'SATA · 7200 rpm',     289.00, 'SATA', 8);

-- Fontes
INSERT INTO componente (categoria, marca, modelo, descricao, preco, potencia_w) VALUES
('fonte', 'Corsair', 'Fonte 550 W 80 Plus Bronze', '550 W', 299.00, 550),
('fonte', 'Corsair', 'Fonte 650 W 80 Plus Bronze', '650 W', 379.00, 650),
('fonte', 'Corsair', 'Fonte 750 W 80 Plus Gold',   '750 W', 549.00, 750),
('fonte', 'Corsair', 'Fonte 850 W 80 Plus Gold',   '850 W', 689.00, 850);

-- Gabinetes
INSERT INTO componente (categoria, marca, modelo, descricao, preco, formatos, max_gpu_mm) VALUES
('gabinete', 'Cooler Master', 'Gabinete Compacto mATX', 'mATX · placa de vídeo até 260 mm',     249.00, 'mATX',         260),
('gabinete', 'NZXT',          'Gabinete Mid Tower',     'mATX/ATX · placa de vídeo até 330 mm', 399.00, 'mATX,ATX',     330),
('gabinete', 'Corsair',       'Gabinete Full Tower',    'ITX/mATX/ATX · placa de vídeo até 400 mm', 749.00, 'ITX,mATX,ATX', 400);
