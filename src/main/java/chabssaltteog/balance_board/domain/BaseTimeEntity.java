package chabssaltteog.balance_board.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass   // 상속시 컬럼 추가
@EntityListeners(AuditingEntityListener.class)  //시간 측정 기능
public abstract class BaseTimeEntity {  //인스턴스 생성 x

    @CreatedDate
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;
}
