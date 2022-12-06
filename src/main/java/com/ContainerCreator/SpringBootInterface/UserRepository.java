package com.ContainerCreator.SpringBootInterface;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ContainerCreator.EC2Creator.Client;

public interface UserRepository extends JpaRepository<Client, String>{

}
