import os
from dataclasses import dataclass


@dataclass
class Settings:
    openai_api_key: str = os.getenv("OPENAI_API_KEY", "")
    openai_chat_model: str = os.getenv("OPENAI_CHAT_MODEL", "gpt-4.1-mini")
    openai_embedding_model: str = os.getenv("OPENAI_EMBEDDING_MODEL", "text-embedding-3-small")
    top_k: int = int(os.getenv("TOP_K", "3"))
    chunk_size: int = int(os.getenv("CHUNK_SIZE", "500"))
    chunk_overlap: int = int(os.getenv("CHUNK_OVERLAP", "50"))


settings = Settings()
