package ch.fhnw.cere.repository.services;

import ch.fhnw.cere.repository.models.FeedbackStatus;
import ch.fhnw.cere.repository.repositories.FeedbackStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FeedbackStatusServiceImpl implements FeedbackStatusService {

    @Autowired
    private FeedbackStatusRepository feedbackStatusRepository;

    @Override
    public List<FeedbackStatus> findAll() {
       return feedbackStatusRepository.findAll();
    }

    @Override
    public FeedbackStatus save(FeedbackStatus feedbackStatus) {
        return feedbackStatusRepository.save(feedbackStatus);
    }

    @Override
    public FeedbackStatus find(long id) {
        return feedbackStatusRepository.findOne(id);
    }

    @Override
    public FeedbackStatus findByStatus(String status) {
        return feedbackStatusRepository.findByStatus(status);
    }

    @Override
    public void delete(long id) {
        feedbackStatusRepository.delete(id);
    }
}
