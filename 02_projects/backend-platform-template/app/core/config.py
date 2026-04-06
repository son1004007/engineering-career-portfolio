import os

class Settings:
    ENV = os.getenv("APP_ENV", "local")
    PORT = int(os.getenv("APP_PORT", "8000"))

settings = Settings()
