INSERT INTO fact_groups (name) VALUES
                                   ('Система охлаждения'),
                                   ('Система зажигания'),
                                   ('Топливная система'),
                                   ('Поршневая группа'),
                                   ('ГРМ и привод');
INSERT INTO fact_group_members (fact_group_id, parameter_name) VALUES
                                                                   -- 🔥 Охлаждение
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Система охлаждения'), 'temperature'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Система охлаждения'), 'coolant_level'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Система охлаждения'), 'radiator_status'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Система охлаждения'), 'thermostat_status'),

                                                                   -- ⚡ Зажигание
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Система зажигания'), 'misfire_codes'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Система зажигания'), 'spark_plug_status'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Система зажигания'), 'ignition_coil_status'),

                                                                   -- ⛽ Топливо
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Топливная система'), 'fuel_pressure'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Топливная система'), 'fuel_pump_status'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Топливная система'), 'injector_status'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Топливная система'), 'fuel_mixture'),

                                                                   -- 🔩 Поршневая группа
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Поршневая группа'), 'compression_drop'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Поршневая группа'), 'oil_consumption'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'Поршневая группа'), 'oil_pressure'),

                                                                   -- ⏳ ГРМ
                                                                   ((SELECT id FROM fact_groups WHERE name = 'ГРМ и привод'), 'timing_belt_status'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'ГРМ и привод'), 'camshaft_sensor_status'),
                                                                   ((SELECT id FROM fact_groups WHERE name = 'ГРМ и привод'), 'crankshaft_sensor_status');

INSERT INTO explanations (rule_id, explanation) VALUES
                                                    ((SELECT id FROM rules WHERE action = 'Перегрев двигателя'), 'Температура выше 100°C, возможен перегрев.'),
    ((SELECT id FROM rules WHERE action = 'Белый дым из выхлопа'), 'Белый дым из выхлопа может свидетельствовать о проблемах с ГБЦ или охлаждающей жидкостью.'),
    ((SELECT id FROM rules WHERE action = 'Критически низкое давление масла'), 'Давление масла критически низкое, возможен отказ двигателя.');

INSERT INTO recommendations (diagnosis, recommendation) VALUES
                                                            ('Перегрев двигателя', 'Проверьте уровень охлаждающей жидкости, радиатор и термостат.'),
                                                            ('Белый дым из выхлопа', 'Осмотрите ГБЦ и систему охлаждения.'),
                                                            ('Критически низкое давление масла', 'Проверьте компрессию и расход масла.');

INSERT INTO rules (parameter_name, comparison_operator, threshold_value, text_value, action, rule_type)
VALUES
    -- 🔥 1️⃣ Симптомы (начальные факты, приходят на вход)
    ('temperature', '>', 100, NULL, 'Перегрев двигателя', 'symptom'),
    ('exhaust_color', '=', NULL, 'white', 'Белый дым из выхлопа', 'symptom'),
    ('exhaust_color', '=', NULL, 'black', 'Чёрный дым из выхлопа', 'symptom'),
    ('oil_pressure', '<', 10, NULL, 'Критически низкое давление масла', 'symptom'),
    ('engine_vibration', '>', 70, NULL, 'Сильные вибрации двигателя', 'symptom'),
    ('fuel_pressure', '<', 30, NULL, 'Проблема с подачей топлива', 'symptom'),
    ('compression_drop', '>', 20, NULL, 'Потеря компрессии', 'symptom'),

    -- 🔍 2️⃣ Промежуточные правила (аналитика эксперта, требуют подтверждения)
    ('coolant_level', '<', 50, NULL, 'Недостаточный уровень охлаждающей жидкости', 'intermediate'),
    ('radiator_status', '=', 0, NULL, 'Радиатор неисправен', 'intermediate'),
    ('thermostat_status', '=', 0, NULL, 'Неисправен термостат', 'intermediate'),
    ('spark_plug_status', '=', 0, NULL, 'Неисправные свечи зажигания', 'intermediate'),
    ('ignition_coil_status', '=', 0, NULL, 'Проблема с катушкой зажигания', 'intermediate'),
    ('oil_consumption', '>', 1, NULL, 'Повышенный расход масла', 'intermediate'),
    ('timing_belt_status', '=', 0, NULL, 'Обрыв ремня ГРМ', 'intermediate'),

    -- 🏁 3️⃣ Финальные диагнозы (итоговая причина, не зависит от конкретного параметра)
    (NULL, '=', NULL, NULL, 'Пробита прокладка ГБЦ', 'final'),
    (NULL, '=', NULL, NULL, 'Износ поршневых колец', 'final'),
    (NULL, '=', NULL, NULL, 'Засорён топливный фильтр', 'final'),
    (NULL, '=', NULL, NULL, 'Обрыв ремня ГРМ', 'final'),
    (NULL, '=', NULL, NULL, 'Перебои в зажигании из-за неисправной катушки', 'final');


INSERT INTO rule_dependencies (parent_rule_id, child_rule_id, requires_confirmation)
VALUES
    -- 🔥 Перегрев двигателя → Проверка охлаждающей системы
    ((SELECT id FROM rules WHERE action = 'Перегрев двигателя'),
    (SELECT id FROM rules WHERE action = 'Недостаточный уровень охлаждающей жидкости'), TRUE),
    ((SELECT id FROM rules WHERE action = 'Перегрев двигателя'),
     (SELECT id FROM rules WHERE action = 'Радиатор неисправен'), TRUE),
    ((SELECT id FROM rules WHERE action = 'Перегрев двигателя'),
     (SELECT id FROM rules WHERE action = 'Неисправен термостат'), TRUE),

    -- 🔥 Критически низкое давление масла → Проверка поршневой группы
    ((SELECT id FROM rules WHERE action = 'Критически низкое давление масла'),
     (SELECT id FROM rules WHERE action = 'Износ поршневых колец'), TRUE),
    ((SELECT id FROM rules WHERE action = 'Критически низкое давление масла'),
     (SELECT id FROM rules WHERE action = 'Повышенный расход масла'), TRUE),

    -- 🔥 Белый дым из выхлопа → Возможные причины
    ((SELECT id FROM rules WHERE action = 'Белый дым из выхлопа'),
     (SELECT id FROM rules WHERE action = 'Пробита прокладка ГБЦ'), TRUE),

    -- 🔥 Потеря компрессии → Возможные причины
    ((SELECT id FROM rules WHERE action = 'Потеря компрессии'),
     (SELECT id FROM rules WHERE action = 'Износ поршневых колец'), TRUE),

    -- 🔥 Проблема с подачей топлива → Проверка системы подачи топлива
    ((SELECT id FROM rules WHERE action = 'Проблема с подачей топлива'),
     (SELECT id FROM rules WHERE action = 'Засорён топливный фильтр'), TRUE),

    -- 🔥 Сильные вибрации двигателя → Возможные причины
    ((SELECT id FROM rules WHERE action = 'Сильные вибрации двигателя'),
     (SELECT id FROM rules WHERE action = 'Обрыв ремня ГРМ'), TRUE),

    -- 🔥 Проблема с катушкой зажигания → Возможные последствия
    ((SELECT id FROM rules WHERE action = 'Проблема с катушкой зажигания'),
     (SELECT id FROM rules WHERE action = 'Перебои в зажигании из-за неисправной катушки'), TRUE);
