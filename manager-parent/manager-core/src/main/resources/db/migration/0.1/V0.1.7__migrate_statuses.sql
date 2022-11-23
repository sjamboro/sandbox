UPDATE BRIDGE SET status='READY' WHERE status='AVAILABLE';
UPDATE BRIDGE SET status='ACCEPTED' WHERE status='REQUESTED';
UPDATE BRIDGE SET status='DEPROVISION' WHERE status='DELETION_REQUESTED';

UPDATE PROCESSOR SET status='READY' WHERE status='AVAILABLE';
UPDATE PROCESSOR SET status='ACCEPTED' WHERE status='REQUESTED';
UPDATE PROCESSOR SET status='DEPROVISION' WHERE status='DELETION_REQUESTED';