package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * ManyWorld는 네트워크상에서의 월드를 의미하는 클래스입니다.
 * ManyWorld와 같이 ProxiedManyWorld 클래스를 참고해주세요.
 *
 */
public interface ManyWorld {

    /**
     * 월드 이름
     * @return 월드 이름
     */
    String getName();

    /**
     * 월드 정보
     * @return
     */
    WorldInform getWorldInfo();

    /**
     * 월드를 적당한 서버에 생성합니다.
     * @param inform 로드할 월드 정보
     * @return 새로 생성된 월드.
     */
    CompletableFuture<ManyWorld> create(WorldInform inform);

    /**
     * 월드를 해당 서버에 생성합니다.
     */
    CompletableFuture<ManyWorld> create(BukkitView view, WorldInform inform);

    /**
     * 월드가 데이터베이스에 존재하는지 확인합니다.
     * @return 존재 여부.
     */
    CompletableFuture<Boolean> isExists();

    /**
     * 현재 로드되어있는 월드 위치입니다.
     * @return 현재 로드되어 있는 월드 위치, 로드되어 있지 않을 수도 있음.
     */
    CompletableFuture<Optional<BukkitView>> getLocated();

    /**
     * 현재 로드 되어 있는지 여부를 확인합니다.
     * @return 로드 되어 있는지 여부.
     */
    CompletableFuture<Boolean> isLoaded();

    /**
     * 해당 월드를 로드합니다.
     * @return 로드된 월드 위치. 로드가 불가능 할 경우 ServerView는 null.
     */
    CompletableFuture<ServerView> load();

    /**
     * 해당 월드를 특정 서버에 로드합니다.
     * @param server 로드 할 서버 위치.
     * @return 로드 된 월드 위치. 로드가 불가능 할 경우 ServerView는 null.
     */
    CompletableFuture<ServerView> load(BukkitView server);

    /**
     * 로드 되어 있는 월드를 언로드 시키고 다른 월드로 옮깁니다.
     * IllegalArgumentException - 해당 서버로 월드를 이동시키지 못할 경우 발생합니다.
     * IllegalStateException - 월드를 해당 월드로 이동 시킬 수 없을 경우 발생합니다.
     * @param view 이동 시킬 월드 위치.
     * @return 이동되고 난 뒤 새로운 월드 객체
     */
    CompletableFuture<ManyWorld> move(BukkitView view);

    /**
     * 로드 되어 있는 월드를 언로드 시킵니다.
     * true - 월드가 이미 언로드 되어 있거나, 성공적으로 언로드 되었을 경우.
     * false - 월드를 정상적으로 언로드 하지 못했음.
     * @return 언로드 성공 여부.
     */
    CompletableFuture<Boolean> unload();

    /**
     * 월드를 복제해서 새로운 로드되지 않은 월드를 생성합니다.
     * @param info 새로 생성될 월드 정보
     * @return 새로 생성된 월드.
     */
    CompletableFuture<ManyWorld> copy(WorldInform info);

}
