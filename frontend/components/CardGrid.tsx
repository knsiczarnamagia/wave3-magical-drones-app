import { getTransformations } from '@/lib/data';
import Card from './Card';
import styles from './CardGrid.module.scss';
import Link from 'next/link';

export default async function CardGrid() {

    const transformations = await getTransformations();

    if (transformations.length === 0) {
        return (
            <div className={styles.fallbackContainer}>
                <p>You don&apos;t have any transformations yet.</p>
                <Link href="/app/model" className={styles.link}>Create one.</Link>
            </div>
        );
    }

    return (
        <div className={styles.grid}>
            {transformations.map((t) => (
                <Link href={`/app/model/${t.id}`} key={t.id} className={styles.link}>
                    <Card
                        key={t.id}
                        imageUuid={t.sourceImageUuid}
                        title={t.title}
                        description={t.description}
                    />
                </Link>
            ))}
        </div>
    );
}
