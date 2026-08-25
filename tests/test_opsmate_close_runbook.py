from pathlib import Path
import unittest


ROOT = Path(__file__).resolve().parents[1]
CLOSE_SCRIPT = ROOT / "02_projects" / "opsmate-local" / "deploy" / "close-demo.sh"


class OpsMateCloseRunbookTest(unittest.TestCase):
    def test_postgres_stop_has_bounded_convergence_before_closed_verification(self):
        script = CLOSE_SCRIPT.read_text(encoding="utf-8")

        stop = 'compose stop --timeout 30 db'
        convergence = 'wait_for_service_stopped db 15'
        verify = '"$SCRIPT_DIR/verify-closed.sh"'

        self.assertIn("wait_for_service_stopped()", script)
        self.assertIn("docker inspect --format '{{.State.Running}}'", script)
        self.assertIn(stop, script)
        self.assertIn(convergence, script)
        self.assertIn(verify, script)
        self.assertLess(script.index(stop), script.index(convergence))
        self.assertLess(script.index(convergence), script.index(verify))

    def test_final_tunnel_secret_cleanup_occurs_after_database_compose_activity(self):
        script = CLOSE_SCRIPT.read_text(encoding="utf-8")

        convergence = 'wait_for_service_stopped db 15'
        final_cleanup = 'remove_tunnel_secret_volume\n\n"$SCRIPT_DIR/verify-closed.sh"'
        verify = '"$SCRIPT_DIR/verify-closed.sh"'

        self.assertIn("remove_tunnel_secret_volume()", script)
        self.assertIn(final_cleanup, script)
        self.assertLess(script.index(convergence), script.index(final_cleanup))
        self.assertLess(script.index(final_cleanup), script.index(verify, script.index(final_cleanup)))

    def test_normal_close_retains_postgres_volume(self):
        script = CLOSE_SCRIPT.read_text(encoding="utf-8")

        self.assertNotIn("down --volumes", script)
        self.assertNotIn("rm --force --stop db", script)
        self.assertIn('secret_volume="${COMPOSE_PROJECT_NAME:-opsmate-demo}-tunnel-secrets"', script)


if __name__ == "__main__":
    unittest.main()
