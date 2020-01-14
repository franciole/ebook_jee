package br.com.framework.interfac.crud;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.framework.hibernate.session.HibernateUtil;
import br.com.framework.implementacao.crud.JdbcTemplateImpl;
import br.com.framework.implementacao.crud.SimpleJdbcInsertImplments;
import br.com.framework.implementacao.crud.SimpleJdbcTemplateImpl;

@Component
@Transactional
public class ImplementacaoCrud<T> implements InterfaceCrud<T> {

	private static final long serialVersionUID = 1L;

	private static SessionFactory sessionFatory = HibernateUtil.getSessionFactory();

	@Autowired
	private JdbcTemplateImpl jdbcTemplate;

	@Autowired
	private SimpleJdbcTemplateImpl simpleJdbcTemplate;

	@Autowired
	private SimpleJdbcInsertImplments simpleJdbcInsert;

	@Autowired
	private SimpleJdbcTemplateImpl simpleJdbcTemplateImpl;

	private SimpleJdbcTemplate getSimpleJdbcTemplateImpl() {
		return simpleJdbcTemplateImpl;
	}

	@Override
	public void save(T obj) throws Exception {
		validaSessionFactory();
		sessionFatory.getCurrentSession().save(obj);
		executeFlusSession();
	}

	@Override
	public void persist(T obj) throws Exception {
		validaSessionFactory();
		sessionFatory.getCurrentSession().persist(obj);
		executeFlusSession();
	}

	@Override
	public void saveOrUpdate(T obj) throws Exception {
		validaSessionFactory();
		sessionFatory.getCurrentSession().saveOrUpdate(obj);
		executeFlusSession();
	}

	@Override
	public void update(T obj) throws Exception {
		validaSessionFactory();
		sessionFatory.getCurrentSession().update(obj);
		executeFlusSession();
	}

	@Override
	public void delete(T obj) throws Exception {
		validaSessionFactory();
		sessionFatory.getCurrentSession().delete(obj);
		executeFlusSession();
	}

	@Override
	public T merge(T obj) throws Exception {
		validaSessionFactory();
		obj = (T) sessionFatory.getCurrentSession().merge(obj);
		executeFlusSession();
		return obj;
	}

	@Override
	public List<T> findList(Class<T> entidade) throws Exception {
		validaSessionFactory();

		StringBuilder query = new StringBuilder();
		query.append(" select distinct(entity) from ").append(entidade.getSimpleName()).append(" entity ");

		List<T> lista = sessionFatory.getCurrentSession().createQuery(query.toString()).list();
		return lista;
	}

	@Override
	public Object findById(Class<T> entidade, Long id) throws Exception {
		validaSessionFactory();
		Object obj = sessionFatory.getCurrentSession().load(getClass(), id);
		return obj;
	}

	@Override
	public T findByPorId(Class<T> entidade, Long id) throws Exception {
		validaSessionFactory();
		T obj = (T) sessionFatory.getCurrentSession().load(getClass(), id);
		return obj;
	}

	@Override
	public List<T> findLysByQueryDinamica(String s) throws Exception {
		validaSessionFactory();
		List<T> lista = new ArrayList<T>();
		lista = sessionFatory.getCurrentSession().createQuery(s).list();
		return lista;
	}

	@Override
	public void executeUpdateQueryDinamica(String s) throws Exception {
		validaSessionFactory();
		sessionFatory.getCurrentSession().createQuery(s).executeUpdate();
		executeFlusSession();
	}

	@Override
	public void executeUpdateSQLDinamica(String s) throws Exception {
		validaSessionFactory();
		sessionFatory.getCurrentSession().createSQLQuery(s).executeUpdate();
		executeFlusSession();
	}

	@Override
	public void clearSession() throws Exception {
		sessionFatory.getCurrentSession().clear();
	}

	@Override
	public void evict(Object objs) throws Exception {
		validaSessionFactory();
		sessionFatory.getCurrentSession().evict(objs);
	}

	@Override
	public Session getSession() throws Exception {
		validaSessionFactory();
		return sessionFatory.getCurrentSession();
	}

	@Override
	public List<?> getListSQLDinamica(String sql) throws Exception {
		validaSessionFactory();
		List<?> lista = sessionFatory.getCurrentSession().createSQLQuery(sql).list();
		return lista;
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}

	@Override
	public SimpleJdbcInsert getSimpleJdbcInsert() {
		return simpleJdbcInsert;
	}

	@Override
	public Long totalRegistro(String table) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" select count(1) from ").append(table);
		return jdbcTemplate.queryForLong(sql.toString());
	}

	@Override
	public Query obterQuery(String query) throws Exception {
		validaSessionFactory();
		Query queryReturn = sessionFatory.getCurrentSession().createQuery(query.toString());
		return queryReturn;
	}

	@Override
	public List<T> findLysByQuery(String query, int iniciaNoRegistro, int maximoResultado) throws Exception {
		validaSessionFactory();
		List<T> lista = new ArrayList<>();
		lista = sessionFatory.getCurrentSession().createQuery(query).setFirstResult(iniciaNoRegistro)
				.setMaxResults(maximoResultado).list();
		return lista;
	}

	private void validaSessionFactory() {
		if (sessionFatory == null) {
			sessionFatory = HibernateUtil.getSessionFactory();
		}
		validaTransaction();
	}

	private void validaTransaction() {
		if (!sessionFatory.getCurrentSession().getTransaction().isActive()) {
			sessionFatory.getCurrentSession().beginTransaction();
		}
	}

	private void comitProcessoAjax() {
		sessionFatory.getCurrentSession().beginTransaction().commit();
	}

	private void rollBackProcessoAjax() {
		sessionFatory.getCurrentSession().beginTransaction().rollback();
	}

	private void executeFlusSession() {
		sessionFatory.getCurrentSession().flush();
	}

	public List<Object[]> getListSQLDinamicaArray(String sql) throws Exception {
		validaSessionFactory();
		List<Object[]> lista = (List<Object[]>) sessionFatory.getCurrentSession().createSQLQuery(sql).list();
		return lista;
	}
}
