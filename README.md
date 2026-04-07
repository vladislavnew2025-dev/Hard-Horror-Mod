# Psychological Horror Mod

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
Скрипт сам пытается подготовить окружение:
1. Проверяет Java 17 (и при необходимости скачивает локальный Temurin JDK 17).
2. Использует `gradle`, если он установлен в системе.
3. Если Gradle нет — скачивает локальный Gradle и генерирует `gradlew`.

После сборки бери jar из:
```bash
forge/build/libs/
```
и копируй в папку `mods` (Forge 1.20.1).

Если раньше была ошибка `Plugin net.minecraftforge.gradle not found`, теперь репозитории для плагина заданы в `forge/settings.gradle`.

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


## Roadmap и тест-команды
- Подробный план: `src/main/resources/ROADMAP_RU.md`
- Forge тест-команды:
  - `/psychhorror fear_get`
  - `/psychhorror fear_set <0..100>`
  - `/psychhorror fear_add <1..100>`
  - `/psychhorror screamer_test`
  - `/psychhorror watcher_test`
  - `/psychhorror anomaly_test`


## Текущее поведение в игре
- Fear HUD показывается в actionbar (строка над хотбаром) раз в ~1 секунду.
- Сообщение `перезапись...` при CRITICAL ограничено кулдауном (не бесконечный спам).
- При HIGH/CRITICAL запускаются аномалии мира (странные звуки блоков + короткий эффект DARKNESS).
