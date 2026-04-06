from app.service.rag_service import RAGService


class DummyStore:
    def __init__(self):
        self.saved_chunks = []
        self.search_result = ["chunk-1", "chunk-2"]

    def add(self, chunks):
        self.saved_chunks.extend(chunks)
        return len(chunks)

    def search(self, question, k):
        return self.search_result


class DummyResponseMessage:
    def __init__(self, content):
        self.content = content


class DummyChoice:
    def __init__(self, content):
        self.message = DummyResponseMessage(content)


class DummyCompletionResponse:
    def __init__(self, content):
        self.choices = [DummyChoice(content)]


class DummyCompletions:
    def create(self, model, messages):
        return DummyCompletionResponse("mocked answer")


class DummyChat:
    def __init__(self):
        self.completions = DummyCompletions()


class DummyClient:
    def __init__(self):
        self.chat = DummyChat()


def build_service():
    service = RAGService()
    service.store = DummyStore()
    service.client = DummyClient()
    return service


def test_split_text_returns_multiple_chunks_for_long_text():
    service = build_service()
    content = "a" * 1200

    chunks = service.split_text(content)

    assert len(chunks) >= 2
    assert all(len(chunk) > 0 for chunk in chunks)


def test_add_document_returns_chunk_count():
    service = build_service()
    content = "sample document for testing"

    result = service.add_document(content)

    assert result["message"] == "Document added"
    assert result["chunks"] >= 1
    assert len(service.store.saved_chunks) == result["chunks"]


def test_query_returns_answer_and_references():
    service = build_service()

    result = service.query("what is this?")

    assert result["question"] == "what is this?"
    assert result["answer"] == "mocked answer"
    assert result["references"] == ["chunk-1", "chunk-2"]


def test_query_returns_empty_message_when_no_documents_found():
    service = build_service()
    service.store.search_result = []

    result = service.query("unknown")

    assert result["question"] == "unknown"
    assert result["answer"] == "관련 문서를 찾을 수 없습니다."
    assert result["references"] == []
