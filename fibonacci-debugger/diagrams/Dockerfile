# Використовуємо офіційний образ PlantUML
FROM plantuml/plantuml

# Встановлюємо робочу директорію
WORKDIR /app

# Копіюємо .puml файл у контейнер
COPY MinimalDebuggerSequence.puml /app/

# Генеруємо діаграму у форматі PNG
CMD ["plantuml", "-tpng", "MinimalDebuggerSequence.puml"]