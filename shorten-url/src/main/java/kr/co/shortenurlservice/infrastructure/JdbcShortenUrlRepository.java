
package kr.co.shortenurlservice.infrastructure;

import kr.co.shortenurlservice.domain.ShortenUrl;
import kr.co.shortenurlservice.domain.ShortenUrlRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcShortenUrlRepository implements ShortenUrlRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcShortenUrlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ShortenUrl> rowMapper = (rs, rowNum) ->
            new ShortenUrl(
                    rs.getString("original_url"),
                    rs.getString("shorten_url_key"),
                    rs.getLong("redirect_count"),
                    rs.getBoolean("active")
            );

    @Override
    public void saveShortenUrl(ShortenUrl shortenUrl) {

        String sql = """
                INSERT INTO shorten_urls
                    (shorten_url_key, original_url, redirect_count, active)
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                shortenUrl.getShortenUrlKey(),
                shortenUrl.getOriginalUrl(),
                shortenUrl.getRedirectCount(),
                shortenUrl.isActive()
        );
    }

    @Override
    public ShortenUrl findShortenUrlByShortenUrlKey(String key) {

        String sql = """
                SELECT *
                FROM shorten_urls
                WHERE shorten_url_key = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, key)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ShortenUrl> findAll(
            String keyword,
            int limit,
            int offset
    ) {

        String sql = """
                SELECT *
                FROM shorten_urls
                WHERE original_url LIKE ?
                ORDER BY shorten_url_key ASC
                LIMIT ? OFFSET ?
                """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                "%" + keyword + "%",
                limit,
                offset
        );
    }

    @Override
    public long count(String keyword) {

        String sql = """
                SELECT COUNT(*)
                FROM shorten_urls
                WHERE original_url LIKE ?
                """;

        Long result = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                "%" + keyword + "%"
        );

        return result == null ? 0L : result;
    }

    @Override
    public void updateStatus(String key, boolean active) {

        String sql = """
                UPDATE shorten_urls
                SET active = ?
                WHERE shorten_url_key = ?
                """;

        jdbcTemplate.update(sql, active, key);
    }

    @Override
    public boolean increaseRedirectCount(String key) {

        String sql = """
                UPDATE shorten_urls
                SET redirect_count = redirect_count + 1
                WHERE shorten_url_key = ?
                  AND active = TRUE
                """;

        return jdbcTemplate.update(sql, key) == 1;
    }

    // 원본 URL 변경
    @Override
    public boolean updateOriginalUrl(
            String key,
            String originalUrl
    ) {

        String sql = """
                UPDATE shorten_urls
                SET original_url = ?
                WHERE shorten_url_key = ?
                """;

        return jdbcTemplate.update(
                sql,
                originalUrl,
                key
        ) == 1;
    }
}
