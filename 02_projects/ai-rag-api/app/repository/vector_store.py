from __future__ import annotations

from typing import List

import faiss
import numpy as np
from openai import OpenAI

from app.core.config import settings
from app.core.logging import get_logger

logger = get_logger(__name__)


class VectorStore:
    def __init__(self) -> None:
        self.dimension = 1536
        self.index = faiss.IndexFlatL2(self.dimension)
        self.documents: List[str] = []
        self.client = OpenAI(api_key=settings.openai_api_key)

    def _embed(self, text: str) -> np.ndarray:
        response = self.client.embeddings.create(
            model=settings.openai_embedding_model,
            input=text,
        )
        embedding = response.data[0].embedding
        return np.array(embedding, dtype="float32")

    def add(self, text_chunks: List[str]) -> int:
        vectors = []
        for chunk in text_chunks:
            vectors.append(self._embed(chunk))
            self.documents.append(chunk)

        if vectors:
            self.index.add(np.array(vectors))
            logger.info("stored %s chunks into vector index", len(vectors))
        return len(vectors)

    def search(self, question: str, k: int) -> List[str]:
        if not self.documents:
            return []

        query_vector = self._embed(question)
        distances, indices = self.index.search(np.array([query_vector]), k)
        logger.info("vector search completed: k=%s, hits=%s", k, len(indices[0]))
        return [self.documents[i] for i in indices[0] if 0 <= i < len(self.documents)]
