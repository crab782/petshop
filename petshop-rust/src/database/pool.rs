use diesel::mysql::MysqlConnection;
use diesel::r2d2::{ConnectionManager, Pool, PooledConnection};
use std::env;

pub type DbPool = Pool<ConnectionManager<MysqlConnection>>;
pub type DbConn = PooledConnection<ConnectionManager<MysqlConnection>>;

static mut POOL: Option<DbPool> = None;

pub fn init_pool() {
    dotenv::dotenv().ok();

    let database_url = env::var("DATABASE_URL").expect("DATABASE_URL must be set");
    let manager = ConnectionManager::<MysqlConnection>::new(database_url);

    let pool = Pool::builder()
        .max_size(10)
        .min_idle(Some(5))
        .build(manager)
        .expect("Failed to create pool");

    unsafe {
        POOL = Some(pool);
    }
}

pub fn get_pool() -> DbPool {
    unsafe {
        POOL.clone().expect("Pool not initialized")
    }
}