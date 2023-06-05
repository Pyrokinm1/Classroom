package classrom.repositories;

import classrom.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Group findGroupByGroupName(String groupName);

    Iterable<Group> findByGroupNameContains(String groupName);
}