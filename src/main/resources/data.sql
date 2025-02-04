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

INSERT INTO rules (rule_name, parameter_name, comparison_operator, threshold_value, text_value, action, rule_type)
VALUES
    -- 🔥 1️⃣ Симптомы (то, что замечает водитель)
    ('high_oil_consumption', NULL, '=', NULL, 'расход масла', 'Возможные причины расхода масла', 'symptom'),
    ('low_power', NULL, '=', NULL, 'потеря мощности', 'Возможные причины потери мощности', 'symptom'),
    ('engine_misfire', NULL, '=', NULL, 'троение двигателя', 'Возможные причины троения двигателя', 'symptom'),
    ('bad_start', NULL, '=', NULL, 'плохой запуск', 'Возможные причины плохого запуска', 'symptom'),

    -- 🔍 2️⃣ Объективные ошибки (то, что выдаёт автомобиль)
    ('low_oil_pressure', 'oil_pressure', '<', 10, NULL, 'Критически низкое давление масла', 'error'),
    ('low_fuel_pressure', 'fuel_pressure', '<', 30, NULL, 'Ошибка давления топлива', 'error'),
    ('ignition_misfire_error', 'ignition_misfire', '=', 1, NULL, 'Ошибка пропусков зажигания', 'error'),

    -- 🔍 3️⃣ Промежуточные причины (анализ эксперта)
    ('compression_loss', 'compression_drop', '>', 20, NULL, 'Потеря компрессии', 'intermediate'),
    ('increased_oil_consumption', 'oil_consumption', '>', 1, NULL, 'Повышенный расход масла', 'intermediate'),
    ('injector_fault', 'fuel_injector_status', '=', 0, NULL, 'Неисправность форсунки', 'intermediate'),
    ('spark_plug_issue', 'spark_plug_status', '=', 0, NULL, 'Проблема со свечами зажигания', 'intermediate'),

    -- 🏁 4️⃣ Финальные неисправности
    ('worn_piston_rings', NULL, '=', NULL, NULL, 'Износ поршневых колец', 'final'),
    ('blown_head_gasket', NULL, '=', NULL, NULL, 'Пробита прокладка ГБЦ', 'final'),
    ('fuel_pump_failure', NULL, '=', NULL, NULL, 'Неисправность топливного насоса', 'final'),
    ('clogged_fuel_filter', NULL, '=', NULL, NULL, 'Засорён топливный фильтр', 'final');


INSERT INTO rule_dependencies (parent_rule_id, child_rule_id, dependency_type)
VALUES
    -- 🔥 Расход масла → Возможные причины
    ((SELECT id FROM rules WHERE rule_name = 'high_oil_consumption'),
     (SELECT id FROM rules WHERE rule_name = 'increased_oil_consumption'), 'single'),
    ((SELECT id FROM rules WHERE rule_name = 'high_oil_consumption'),
     (SELECT id FROM rules WHERE rule_name = 'compression_loss'), 'single'),

    -- 🔥 Потеря мощности → Возможные причины
    ((SELECT id FROM rules WHERE rule_name = 'low_power'),
     (SELECT id FROM rules WHERE rule_name = 'spark_plug_issue'), 'single'),
    ((SELECT id FROM rules WHERE rule_name = 'low_power'),
     (SELECT id FROM rules WHERE rule_name = 'injector_fault'), 'single'),

    -- 🔥 Ошибки → Причины
    ((SELECT id FROM rules WHERE rule_name = 'low_oil_pressure'),
     (SELECT id FROM rules WHERE rule_name = 'worn_piston_rings'), 'single'),
    ((SELECT id FROM rules WHERE rule_name = 'low_fuel_pressure'),
     (SELECT id FROM rules WHERE rule_name = 'fuel_pump_failure'), 'single'),

    -- 🔥 Троение двигателя + Плохой запуск → Свечи
    ((SELECT id FROM rules WHERE rule_name = 'engine_misfire'),
     (SELECT id FROM rules WHERE rule_name = 'spark_plug_issue'), 'single'),
    ((SELECT id FROM rules WHERE rule_name = 'bad_start'),
     (SELECT id FROM rules WHERE rule_name = 'spark_plug_issue'), 'single');
