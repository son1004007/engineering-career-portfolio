from fastapi import FastAPI
from app.api.routes import router

app = FastAPI(title="Backend Platform Template")

app.include_router(router)
