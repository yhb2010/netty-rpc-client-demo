package com.demo.netty.rpc.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person implements Serializable {

	private String firstName;
    private String lastName;

}
