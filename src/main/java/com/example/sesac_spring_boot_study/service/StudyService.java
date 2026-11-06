package com.example.sesac_spring_boot_study.service;

import com.example.sesac_spring_boot_study.domain.StudyMember;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyService {

    private final List<StudyMember> memberList = new ArrayList<>();

    private long nextId = 1L;
    private int capacity = 2;


    public String register(String name){

        String realName = name.trim();

        if(realName==null || realName.isEmpty()){
            return "신청 실패 : 입력 오류";
        }

        for(StudyMember member : memberList){
            if(member.getName().equals(realName)){
                return "신청 실패 : 중복 이름";
            }
        }

        long count = memberList.stream()
                .filter(StudyMember::isConfirm)
                .count();

        boolean confirm = count < capacity;

        StudyMember member= new StudyMember(nextId++,realName, confirm);
        memberList.add(member);

        // 3. 신청 성공
        return "id= " + member.getId() + ", 이름= " + member.getName() + ", 상태= " + (confirm ? "확정" : "대기");
    }

    public String cancel(String memberId){

        if (memberId == null || memberId.trim().isEmpty()) {
            return "취소 실패: ID 입력";
        }

        long id;
        try {
            id = Long.parseLong(memberId.trim());
        } catch (NumberFormatException e) {
            return "취소 실패: ID 숫자 입력";
        }

        StudyMember targetMember = null;

        for (StudyMember member : memberList) {
            if (member.getId().equals(id)) {
                targetMember = member;
                break;
            }
        }

        if (targetMember == null) {
            return "취소 실패: 존재하지 않는 ID";
        }

        boolean confirm = targetMember.isConfirm();
        memberList.remove(targetMember);

        String result = "취소 성공: " + targetMember.getName();

        if (confirm) {
            for (StudyMember member : memberList) {
                if (!member.isConfirm()) {
                    member.setConfirmed(true);
                    result += ", 대기자 승격: " + member.getName();
                    break;
                }
            }
        }

        return result;
    }

    public String changeCapacity(String value){

        if (value == null || value.trim().isEmpty()) {
            return "변경 실패: 정원을 입력. 현재 정원=" + value;
        }

        int newCapacity;

        try {
            newCapacity = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return "변경 실패: 정원은 정수. 현재 정원=" + value;
        }

        if (newCapacity < 1 || newCapacity > 10) {
            return "변경 실패: 정원은 1~10명. 현재 정원=" + value;
        }

        long confirmedCount = memberList.stream()
                .filter(StudyMember::isConfirm)
                .count();

        if (newCapacity < confirmedCount) {
            return "변경 실패: 현재 확정 인원보다 작게 설정 불가. 현재 정원=" + capacity;
        }

        capacity = newCapacity;

        int avail = capacity - (int) confirmedCount;
        List<String> promoted = new ArrayList<>();

        for (StudyMember member : memberList) {

            if (avail == 0) {
                break;
            }

            if (!member.isConfirm()) {
                member.setConfirmed(true);
                promoted.add(member.getName());
                avail--;
            }
        }

        return "변경 성공: 현재 정원=" + capacity  + ", 승격자=" + (promoted.isEmpty() ? "없음" : String.join(", ", promoted));
    }

    public String getMembers() {

        if (memberList.isEmpty()) {
            return "참가자가 없습니다";
        }

        StringBuilder result = new StringBuilder();

        result.append("현재 정원: ")
                .append(capacity)
                .append("\n\n");

        for (StudyMember member : memberList) {
            result.append("id=").append(member.getId())
                    .append(", 이름=").append(member.getName())
                    .append(", 상태=")
                    .append(member.isConfirm() ? "확정" : "대기")
                    .append("\n");
        }

        return result.toString();
    }


}
