# JPA

## 1단계 - 엔티티 매핑

### 요구 사항

- [X] 아래의 DDL (Data Definition Language) 을 보고 엔티티 클래스와 레포지토리 클래스 작성
- [X] `@DataJpaTest`를 사용하여 테스트 작성

```sql
create table answer
(
    id          bigint generated by default as identity,
    contents    clob,
    created_at  timestamp not null,
    deleted     boolean   not null,
    question_id bigint,
    updated_at  timestamp,
    writer_id   bigint,
    primary key (id)
)
```

```sql
create table delete_history
(
    id            bigint generated by default as identity,
    content_id    bigint,
    content_type  varchar(255),
    create_date   timestamp,
    deleted_by_id bigint,
    primary key (id)
)
```

```sql
create table question
(
    id         bigint generated by default as identity,
    contents   clob,
    created_at timestamp    not null,
    deleted    boolean      not null,
    title      varchar(100) not null,
    updated_at timestamp,
    writer_id  bigint,
    primary key (id)
)
```

```sql
create table user
(
    id         bigint generated by default as identity,
    created_at timestamp   not null,
    email      varchar(50),
    name       varchar(20) not null,
    password   varchar(20) not null,
    updated_at timestamp,
    user_id    varchar(20) not null,
    primary key (id)
)

alter table user
    add constraint UK_a3imlf41l37utmxiquukk8ajc unique (user_id)
```

## 2단계 - 연관 관계 매핑

### 요구 사항

- [ ] 객체의 참조와 테이블의 외래 키를 매핑해서 객체에서는 참조를 사용하고 테이블에서는 외래 키를 사용할 수 있도록 한다.

- H2

```sql
alter table answer
    add constraint fk_answer_to_question
        foreign key (question_id)
            references question

alter table answer
    add constraint fk_answer_writer
        foreign key (writer_id)
            references user

alter table delete_history
    add constraint fk_delete_history_to_user
        foreign key (deleted_by_id)
            references user

alter table question
    add constraint fk_question_writer
        foreign key (writer_id)
            references user
```

- Mysql
```sql
alter table answer
    add constraint fk_answer_to_question
        foreign key (question_id)
            references question (id)

alter table answer
    add constraint fk_answer_writer
        foreign key (writer_id)
            references user (id)

alter table delete_history
    add constraint fk_delete_history_to_user
        foreign key (deleted_by_id)
            references user (id)

alter table question
    add constraint fk_question_writer
        foreign key (writer_id)
            references user (id)
```

## 3단계 - 질문 삭제하기 리팩터링

### 요구 사항

- [X] 질문 데이터를 완전히 삭제하는 것이 아니라 데이터의 상태를 삭제 상태(deleted - boolean type)로 변경한다.
- [X] 로그인 사용자와 질문한 사람이 같은 경우 삭제할 수 있다.
- [X] 답변이 없는 경우 삭제가 가능하다.
- [X] 질문자와 답변 글의 모든 답변자 같은 경우 삭제가 가능하다.
- [ ] 질문을 삭제할 때 답변 또한 삭제해야 하며, 답변의 삭제 또한 삭제 상태(deleted)를 변경한다.
- [ ] 질문자와 답변자가 다른 경우 답변을 삭제할 수 없다.
- [ ] 질문과 답변 삭제 이력에 대한 정보를 DeleteHistory를 활용해 남긴다.
