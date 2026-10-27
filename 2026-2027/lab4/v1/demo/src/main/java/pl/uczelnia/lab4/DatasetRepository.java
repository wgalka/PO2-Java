package pl.uczelnia.lab4;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class DatasetRepository {
    private final SessionFactory sessionFactory;

    public DatasetRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(EconomicDataset dataset) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                if (dataset.getId() == null) {
                    session.persist(dataset);
                } else {
                    session.merge(dataset);
                }
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    public Optional<EconomicDataset> findByIdWithDetails(Long id) {
        try (Session session = sessionFactory.openSession()) {
            String hql = """
                    select d from EconomicDataset d
                    left join fetch d.observations
                    left join fetch d.reports
                    where d.id = :id
                    """;
            EconomicDataset result = session.createQuery(hql, EconomicDataset.class)
                    .setParameter("id", id)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public List<DatasetSummaryDto> findAllSummaries() {
        try (Session session = sessionFactory.openSession()) {
            String hql = """
                    select new pl.uczelnia.lab4.DatasetSummaryDto(
                        d.id,
                        d.code,
                        d.title,
                        count(distinct o.id),
                        count(distinct r.id)
                    )
                    from EconomicDataset d
                    left join d.observations o
                    left join d.reports r
                    group by d.id, d.code, d.title
                    order by d.code
                    """;
            return session.createQuery(hql, DatasetSummaryDto.class).getResultList();
        }
    }

    public boolean delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                EconomicDataset dataset = session.get(EconomicDataset.class, id);
                if (dataset != null) {
                    session.remove(dataset);
                    tx.commit();
                    return true;
                }
                tx.rollback();
                return false;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}
