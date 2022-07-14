package shop.tryit.repository.notice;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import shop.tryit.domain.notice.Notice;
import shop.tryit.domain.notice.NoticeRepository;


@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeJpaRepository jpaRepository;

    @Override
    public Long save(Notice notice) {
        return jpaRepository.save(notice)
                .getId();
    }

    @Override
    public Optional<Notice> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void delete(Notice notice) {
        jpaRepository.delete(notice);
    }

}
