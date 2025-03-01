package org.hyeonqz.architecturelab.clean.example1.adapter.output;

import org.hyeonqz.architecturelab.clean.example1.application.port.output.AddMerchantTerminalPort;
import org.hyeonqz.architecturelab.clean.example1.domain.MerchantTerminal;
import org.hyeonqz.architecturelab.clean.example1.domain.MerchantTerminalDomain;
import org.hyeonqz.architecturelab.clean.example1.mappers.MerchantTerminalMapper;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class AddMerchantTerminalAdapter implements AddMerchantTerminalPort {

//  ⭐️ 위 Adapter 는 영속성 관련 된 부분을 처리한다 -> 아래 구조 처럼 사용하면 된다.
    //private final MerchantTerminalRepository merchantTerminalRepository;

    @Override
    public MerchantTerminalDomain save(MerchantTerminalDomain merchantTerminalDomain) {

        // domain -> to entity
        //MerchantTerminal merchantTerminal = MerchantTerminal.builder().build();

        // save
        //merchantTerminalRepository.save(merchantTerminal);

        // entity -> domain
        //MerchantTerminalDomain merchantTerminalDomain = MerchantTerminalMapper.toDomain();

        return merchantTerminalDomain;
    }

}
