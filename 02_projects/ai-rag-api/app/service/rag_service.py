from __future__ import annotations

from typing import List

from openai import OpenAI

from app.core.config import settings
from app.core.logging import get_logger
from app.repository.vector_store import VectorStore

logger = get_logger(__name__)


class RAGService:
    def __init__(self) -> None:
        self.store = VectorStore()
        self.client = OpenAI(api_key=settings.openai_api_key)

    def split_text(self, content: str) -> List[str]:
        chunk_size = settings.chunk_size
        overlap = settings.chunk_overlap
        chunks: List[str] = []
        start = 0

        while start < len(content):
            end = min(start + chunk_size, len(content))
            chunks.append(content[start:end])
            if end == len(content):
                break
            start = max(end - overlap, 0)

        logger.info("text split into %s chunks", len(chunks))
        return chunks

    def add_document(self, content: str) -> dict:
        chunks = self.split_text(content)
        stored_count = self.store.add(chunks)
        return {
            "message": "Document added",
            "chunks": stored_count,
        }

    def query(self, question: str) -> dict:
        docs = self.store.search(question, k=settings.top_k)
        if not docs:
            return {
                "question": question,
                "answer": "관련 문서를 찾을 수 없습니다.",
                "references": [],
            }

        context = "\n\n".join(docs)
        prompt = f"""
다음 참고 문서를 기반으로 질문에 답하세요.

[참고 문서]
{context}

[질문]
{question}
"""

        response = self.client.chat.completions.create(
            model=settings.openai_chat_model,
            messages=[
                {
                    "role": "user",
                    "content": prompt,
                }
            ],
        )
        answer = response.choices[0].message.content
        logger.info("llm response generated")

        return {
            "question": question,
            "answer": answer,
            "references": docs,
        }
