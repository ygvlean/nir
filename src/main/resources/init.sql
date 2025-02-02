CREATE TABLE rules (
                       id SERIAL PRIMARY KEY,
                       parameter_name TEXT, -- Может быть NULL для финальных выводов
                       comparison_operator VARCHAR(10), -- Оператор сравнения (NULL для финальных выводов)
                       threshold_value DOUBLE PRECISION, -- Числовое значение (NULL, если текстовый параметр или финальный вывод)
                       text_value TEXT, -- Альтернативное текстовое значение (NULL для числовых)
                       action TEXT NOT NULL, -- Действие (вывод эксперта)
                       rule_type TEXT CHECK (rule_type IN ('symptom', 'intermediate', 'final')) NOT NULL -- Тип правила
);

CREATE TABLE rule_dependencies (
                                   id SERIAL PRIMARY KEY,
                                   parent_rule_id INT REFERENCES rules(id) ON DELETE CASCADE, -- Родительское правило
                                   child_rule_id INT REFERENCES rules(id) ON DELETE CASCADE, -- Зависимое правило
                                   dependency_type TEXT CHECK (dependency_type IN ('single', 'combination')) NOT NULL -- Тип связи
);

CREATE TABLE rule_combinations (
                                   id SERIAL PRIMARY KEY,
                                   combination_id INT NOT NULL, -- Группа правил, которые должны сработать вместе
                                   rule_id INT REFERENCES rules(id) ON DELETE CASCADE -- Одно из правил в комбинации
);

CREATE TABLE diagnostic_data (
                                 id SERIAL PRIMARY KEY,
                                 vehicle_id TEXT NOT NULL,
                                 parameter_name TEXT NOT NULL,
                                 parameter_value TEXT NOT NULL, -- Значение может быть и числом, и текстом
                                 source TEXT CHECK (source IN ('symptom', 'full_data')) -- Источник данных (симптом или полный набор)
);
CREATE TABLE fact_groups (
                             id SERIAL PRIMARY KEY,
                             name TEXT NOT NULL UNIQUE -- Название группы (например, "Система охлаждения", "Топливная система")
);

CREATE TABLE fact_group_members (
                                    id SERIAL PRIMARY KEY,
                                    fact_group_id INT REFERENCES fact_groups(id) ON DELETE CASCADE,
                                    parameter_name TEXT NOT NULL UNIQUE -- Название параметра
);

CREATE TABLE explanations (
                              id SERIAL PRIMARY KEY,
                              rule_id INT REFERENCES rules(id) ON DELETE CASCADE,
                              explanation TEXT NOT NULL -- Подробное объяснение причины неисправности
);

CREATE TABLE recommendations (
                                 id SERIAL PRIMARY KEY,
                                 diagnosis TEXT NOT NULL UNIQUE, -- Название диагноза (должно совпадать с `rules.action`)
                                 recommendation TEXT NOT NULL -- Рекомендация по устранению неисправности
);

CREATE TABLE diagnostic_logs (
                                 id SERIAL PRIMARY KEY,
                                 vehicle_id TEXT NOT NULL,
                                 diagnosis TEXT NOT NULL,
                                 explanation TEXT NOT NULL,
                                 timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Время диагностики
);