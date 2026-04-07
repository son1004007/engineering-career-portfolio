from fastapi import APIRouter
from pydantic import BaseModel
from app.service.audit_service import AuditService

router = APIRouter()
service = AuditService()

class AuditRequest(BaseModel):
    actor: str
    action: str
    resource: str
    detail: str

@router.post("/audit")
def create_log(req: AuditRequest):
    return service.create_log(req)

@router.get("/audit")
def get_logs():
    return service.get_logs()
