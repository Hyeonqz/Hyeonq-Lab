package org.hyeonqz.springlab.annotation;

import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

@Configuration
public class CustomBeanRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions (AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
		BeanNameGenerator importBeanNameGenerator) {

		ClassPathScanningCandidateComponentProvider scanner =
			new ClassPathScanningCandidateComponentProvider(false);

		// CustomBean 어노테이션이 붙은 클래스만 검색
		scanner.addIncludeFilter(new AnnotationTypeFilter(CustomBean.class));

		String basePackage = "org.hyeonqz.springlab";
		Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);

		for (BeanDefinition beanDefinition : candidateComponents) {
			try {
				// 클래스 이름 가져오기
				String className = beanDefinition.getBeanClassName();
				if (className == null) continue;

				// 빈 이름 가져오기
				Class<?> clazz = Class.forName(className);
				CustomBean customBeanAnnotation = clazz.getAnnotation(CustomBean.class);
				String beanName = customBeanAnnotation.value().isEmpty()
					? clazz.getSimpleName() // 값이 없으면 클래스 이름으로 등록
					: customBeanAnnotation.value();

				// 빈 정의 생성
				GenericBeanDefinition definition = new GenericBeanDefinition();
				definition.setBeanClass(clazz);

				// 빈 등록
				registry.registerBeanDefinition(beanName, definition);
				System.out.println("Bean registered: " + beanName);

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}