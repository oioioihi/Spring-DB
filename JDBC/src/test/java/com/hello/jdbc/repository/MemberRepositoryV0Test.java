package com.hello.jdbc.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.hello.jdbc.domain.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MemberRepositoryV0Test {
MemberRepositoryV0 repository = new MemberRepositoryV0();

	@Test
	void crud() throws SQLException {

		// save
		Member member = new Member();
		member.setMemberId("membervo4");
		member.setMoney(1000);
		repository.save(member);

		//findById
		Member findMember = repository.findById(member.getMemberId());
		log.info("findMember={}",findMember);
		log.info("member == findMember {}", member == findMember); // false => 서로 다른 인스턴스이다.
		log.info("member equals findMember {}", member.equals(findMember)); // true => 필드 값이 같으면 같은 객체라고 판단.
		assertThat(findMember).isEqualTo(member);

		// update: money 10000 -> 20000
		repository.update(member.getMemberId(),20000);
		Member updatedMember = repository.findById(member.getMemberId());
		assertThat(updatedMember.getMoney()).isEqualTo(20000);

		//delete
		repository.delete(member.getMemberId());
		assertThatThrownBy(()-> repository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
		//  지정한 예외인지 확인 로직확인 함

	}

}