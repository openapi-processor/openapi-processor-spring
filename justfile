default:
  @just --list --unsorted

# update gradle wrapper
wrapper version="8.7":
    ./gradlew wrapper --gradle-version={{version}}
