package es.uvigo.ei.sing.pandrugsdb.service.entity;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

@XmlRootElement(name = "user-metadatas", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserMetadatas {
	@NotNull
	private List<UserMetadata> users;
	
	public UserMetadatas() {}
	
	public static UserMetadatas buildFor(List<User> user) {
		return new UserMetadatas(user.stream().map(UserMetadata::new).collect(toList()));
	}

	public UserMetadatas(List<UserMetadata> users) {
		this.users = users;
	}

	public List<UserMetadata> getUsers() {
		return users;
	}

	public void setUsers(List<UserMetadata> users) {
		this.users = users;
	}
}
