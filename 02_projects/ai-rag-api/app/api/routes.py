from fastapi import APIRouter
from pydantic import BaseModel

from app.service.rag_service import RAGService

router = APIRouter()
service = RAGService()


class DocumentRequest(BaseModel):
    content: str


class QueryRequest(BaseModel):
    question: str


@router.get("/")
def health_check() -> dict:
    return {"message": "ai-rag-api is running"}


@router.post("/documents")
def add_document(request: DocumentRequest) -> dict:
    return service.add_document(request.content)


@router.post("/query")
def query_document(request: QueryRequest) -> dict:
    return service.query(request.question)
