# Hard Horror Mod

Каркас психологического хоррор-мода для Minecraft.

## Что сейчас в репозитории
- Логика мода: `src/main/java/com/hardhorror`
- Forge bridge-классы: `src/main/java/com/hardhorror/forge`
- Отдельный Forge-проект: `forge/`
- Быстрые скрипты проверки логики: `build_quick.sh`, `build_quick.bat`

## Важно
- `build_quick.*` — это быстрая проверка Java-логики.
- Для **реального jar в папку mods** используй `forge/build_mod.sh`.

## Как собрать рабочий Forge jar (Linux)
```bash
cd forge
./build_mod.sh
```
Скрипт сам пытается подготовить Gradle wrapper:
1. Использует `gradle`, если он установлен в системе.
2. Если Gradle нет — скачивает локальный Gradle и генерирует `gradlew`.

После сборки бери jar из:
```bash
forge/build/libs/
```
и копируй в папку `mods` (Forge 1.20.1).

## Как запустить клиент Forge (Linux)
```bash
cd forge
./run_client.sh
```

## Быстрая проверка логики (без Forge)
```bash
./build_quick.sh
```
Создаёт логи в `logs/` и тестовый jar в `dist/`.
