package com.rebate.common.data.seq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Sequence {
    private static final Log log = LogFactory.getLog(Sequence.class);
    private int blockSize = 5;
    private long startValue = 0L;
    private static final String GET_SQL = "select id from sequence_value where name = ?";
    private static final String NEW_SQL = "insert into sequence_value (id,name) values (?,?)";
    private static final String UPDATE_SQL = "update sequence_value set id = ?  where name = ? and id = ?";
    private Map<String, Sequence.Step> stepMap = new HashMap();
    private DataSource dataSource;

    public Sequence() {
    }

    private boolean getNextBlock(String sequenceName, Sequence.Step step) {
        Long value = this.getPersistenceValue(sequenceName);
        if(value == null) {
            try {
                value = this.newPersistenceValue(sequenceName);
            } catch (Exception var5) {
                log.error("newPersistenceValue error!");
                value = this.getPersistenceValue(sequenceName);
            }
        }

        boolean b = this.saveValue(value.longValue(), sequenceName) == 1;
        if(b) {
            step.setCurrentValue(value.longValue());
            step.setEndValue(value.longValue() + (long)this.blockSize);
        }

        return b;
    }

    public synchronized long get(String sequenceName) {
        Sequence.Step step = (Sequence.Step)this.stepMap.get(sequenceName);
        if(step == null) {
            step = new Sequence.Step(this.startValue, this.startValue + (long)this.blockSize);
            this.stepMap.put(sequenceName, step);
        } else if(step.currentValue < step.endValue) {
            return step.incrementAndGet();
        }

        for(int i = 0; i < this.blockSize; ++i) {
            if(this.getNextBlock(sequenceName, step)) {
                return step.incrementAndGet();
            }
        }

        throw new RuntimeException("No more value.");
    }

    private int saveValue(long value, String sequenceName) {
        Connection connection = null;
        PreparedStatement statement = null;

        int var8;
        try {
            connection = this.dataSource.getConnection();
            statement = connection.prepareStatement("update sequence_value set id = ?  where name = ? and id = ?");
            statement.setLong(1, value + (long)this.blockSize);
            statement.setString(2, sequenceName);
            statement.setLong(3, value);
            var8 = statement.executeUpdate();
        } catch (Exception var18) {
            log.error("newPersistenceValue error!", var18);
            throw new RuntimeException("newPersistenceValue error!", var18);
        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException var17) {
                    log.error("close statement error!", var17);
                }
            }

            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException var16) {
                    log.error("close connection error!", var16);
                }
            }

        }

        return var8;
    }

    private Long getPersistenceValue(String sequenceName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.dataSource.getConnection();
            statement = connection.prepareStatement("select id from sequence_value where name = ?");
            statement.setString(1, sequenceName);
            resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long var7 = Long.valueOf(resultSet.getLong("id"));
                return var7;
            }
        } catch (Exception var23) {
            log.error("getPersistenceValue error!", var23);
            throw new RuntimeException("getPersistenceValue error!", var23);
        } finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException var22) {
                    log.error("close resultset error!", var22);
                }
            }

            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException var21) {
                    log.error("close statement error!", var21);
                }
            }

            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException var20) {
                    log.error("close connection error!", var20);
                }
            }

        }

        return null;
    }

    private Long newPersistenceValue(String sequenceName) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = this.dataSource.getConnection();
            statement = connection.prepareStatement("insert into sequence_value (id,name) values (?,?)");
            statement.setLong(1, this.startValue);
            statement.setString(2, sequenceName);
            statement.executeUpdate();
        } catch (Exception var15) {
            log.error("newPersistenceValue error!", var15);
            throw new RuntimeException("newPersistenceValue error!", var15);
        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException var14) {
                    log.error("close statement error!", var14);
                }
            }

            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException var13) {
                    log.error("close connection error!", var13);
                }
            }

        }

        return Long.valueOf(this.startValue);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public void setStartValue(long startValue) {
        this.startValue = startValue;
    }

    static class Step {
        private long currentValue;
        private long endValue;

        Step(long currentValue, long endValue) {
            this.currentValue = currentValue;
            this.endValue = endValue;
        }

        public void setCurrentValue(long currentValue) {
            this.currentValue = currentValue;
        }

        public void setEndValue(long endValue) {
            this.endValue = endValue;
        }

        public long incrementAndGet() {
            return ++this.currentValue;
        }
    }
}
